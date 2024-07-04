package com.sxf.project.service.impl;

import com.sxf.project.dto.MonthlySalaryDTO;
import com.sxf.project.dto.WorkerDTO;
import com.sxf.project.entity.*;
import com.sxf.project.repository.*;
import com.sxf.project.service.WorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
@Service
public class WorkerServiceImpl implements WorkerService {

    private static final Logger logger = LoggerFactory.getLogger(WorkerServiceImpl.class);

    @Autowired
    WorkerRepository workerRepository;

    @Autowired
    FilialRepository filialRepository;

    @Autowired
    MonthlySalaryRepository monthlySalaryRepository;

    @Autowired
    MonthlySalaryPaymentRepository monthlySalaryPaymentRepository;

    @Autowired
    FileRepository fileRepository;

    @Override
    public Page<Worker> getAll(Pageable pageable) throws Exception {
        return workerRepository.findAll(pageable);
    }

    @Override
    public Page<Worker> getAllByFilial(Long filialId, Pageable pageable) {
        return workerRepository.findByFilialId(filialId, pageable);
    }


    @Override
    public Optional<Worker> getById(Long id, User currentUser) throws Exception {

        Optional<Worker> optionalWorker = workerRepository.findById(id);
        if (optionalWorker.isPresent()) {
            Worker checkWorker = optionalWorker.get();
            if (!checkWorker.getFilial().getId().equals(currentUser.getAssignedFilial().getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
                throw new AccessDeniedException("Restricted for this manager");
            }
        }
        return workerRepository.findById(id);
    }

    @Override
    public Optional<Worker> create(WorkerDTO data, User currentUser) throws Exception {
        Optional<Filial> optionalFilial = filialRepository.findById(data.getFilialID());

        // Check if the Filial exists
        if (!optionalFilial.isPresent()) {
            logger.info("Such ID filial does not exist!");

            Filial checkFilial = optionalFilial.get();
            if (!checkFilial.getId().equals(currentUser.getAssignedFilial().getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
                throw new AccessDeniedException("Restricted for this manager");
            }
            return Optional.empty();

        }



        // Create a new Worker
        Worker worker = new Worker();
        worker.setName(data.getName());
        worker.setSurname(data.getSurname());
        worker.setJobDescription(data.getJobDescription());
        worker.setFilial(optionalFilial.get());

        return Optional.of(workerRepository.save(worker));
    }

    @Override
    public Optional<Worker> update(Long id, WorkerDTO data, User currentUser) throws Exception {
        Optional<Worker> optionalWorker = workerRepository.findById(id);

        Worker checkWorker = optionalWorker.get();
        if(!checkWorker.getFilial().getId().equals(currentUser.getAssignedFilial().getId())) {
            logger.info("Restricted for this manager");
            return Optional.empty();
        }

        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();

            FileEntity oldFileEntity = worker.getFileEntity();
            if (data.getFileEntityId() != null) {
                Optional<FileEntity> newFileEntityOptional = fileRepository.findById(data.getFileEntityId());

                if (!newFileEntityOptional.isPresent()) {
                    logger.info("FileEntity with id " + data.getFileEntityId() + " does not exist");
                    return Optional.empty();
                }

                FileEntity newFileEntity = newFileEntityOptional.get();
                worker.setFileEntity(newFileEntity);
            } else {
                if (oldFileEntity != null) {
                    fileRepository.delete(oldFileEntity);
                    worker.setFileEntity(null);
                }
            }
            worker.setName(data.getName());
            worker.setSurname(data.getSurname());
            worker.setJobDescription(data.getJobDescription());

            // Save the updated worker
            return Optional.of(workerRepository.save(worker));
        } else {
            // Handle the case where the worker with the given ID doesn't exist
            throw new ChangeSetPersister.NotFoundException();
        }
    }
    @Override
    public Page<Worker> getAllByNameAndSurnameContains(String name,String surname, Pageable pageable) {
        return workerRepository.findAllByNameAndSurnameContains(name, surname, pageable);
    }

    @Override
    public void deleteById(Long id, User currentUser) {
        Optional<Worker> optionalWorker = workerRepository.findById(id);


        if (optionalWorker.isPresent()) {
            Worker checkWorker = optionalWorker.get();
            if (!checkWorker.getFilial().getId().equals(currentUser.getAssignedFilial().getId())) {
                throw new AccessDeniedException("Restricted for this manager");
            }
        }
        List<MonthlySalaryPayment> paymentsToDelete = monthlySalaryPaymentRepository.findAllByMonthlySalaryId(id);
        monthlySalaryPaymentRepository.deleteAll(paymentsToDelete);
        monthlySalaryRepository.deleteAll(monthlySalaryRepository.findAllByWorkerId(id));
        workerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<MonthlySalary> createForSchedule(MonthlySalaryDTO data) throws Exception {

        List<Worker> allWorkers = workerRepository.findAll();

        for (Worker worker : allWorkers) {

            worker.setCurrentSalary(data.getPaymentAmount());
            workerRepository.save(worker);


            MonthlySalary monthlySalary = new MonthlySalary();

            data.setPropertiesForFirstDay();
            monthlySalary.setMonthDate(data.getMonthDate());
            monthlySalary.setStatus(PaymentStatus.valueOf(data.getStatus()));
            monthlySalary.setPaymentAmount(data.getPaymentAmount());
            monthlySalary.setPaidAmount(data.getPaidAmount());
            monthlySalary.setWorker(worker);


            return Optional.of(monthlySalaryRepository.save(monthlySalary));
        }
        return Optional.empty();
    }
}
