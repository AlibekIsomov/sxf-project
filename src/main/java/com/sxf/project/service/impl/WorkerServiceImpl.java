package com.sxf.project.service.impl;

import com.sxf.project.dto.MonthlySalaryDTO;
import com.sxf.project.dto.WorkerDTO;
import com.sxf.project.entity.*;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.repository.*;
import com.sxf.project.service.WorkerService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
    public List<Worker> getAllByFilial(Long id) {
        return workerRepository.findAllByFilialId(id);
    }

    @Override
    public List<Worker> getWorkerByFilial(User user) {
        if (user.getAssignedFilial() != null && user.getAssignedFilial().getId() != null) {
            return workerRepository.findAllByFilialId(user.getAssignedFilial().getId());
        } else {
            logger.info("User is not assigned to any filial");
            ResponseEntity.badRequest().body("Siz uchun hech qanday filial bog'lanmagan!");
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Worker> getById(Long id, User currentUser) throws Exception {

        Optional<Worker> optionalWorker = workerRepository.findById(id);

        Worker checkWorker = optionalWorker.get();
        Filial workerFilial = checkWorker.getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUserFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            return Optional.empty();
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(workerFilial.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
            return Optional.empty();
        }
        if (!workerRepository.existsById(id)) {
            logger.info("Input with id " + id + " does not exists");
        }
        if (!optionalWorker.isPresent()) {
            return Optional.empty();
        }
        return workerRepository.findById(id);
    }


    @Override
    public ApiResponse create(WorkerDTO data, User currentUser) throws Exception {
        Optional<Filial> optionalFilial = filialRepository.findById(data.getFilialId());

        if (!optionalFilial.isPresent()) {
            logger.info("Such ID filial does not exist!");
            return new ApiResponse("Such ID filial does not exist!", false);
        }

        Filial checkFilial = optionalFilial.get();
        Filial assignedFilial = currentUser.getAssignedFilial();

        if (assignedFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            return new ApiResponse("Restricted: User does not have an assigned filial and is not an ADMIN", false);
        }

        if (assignedFilial != null && !assignedFilial.getId().equals(checkFilial.getId())) {
            logger.info("Restricted: User's assigned filial does not match the checkFilial");
            return new ApiResponse("Restricted: User's assigned filial does not match the checkFilial", false);
        }

        Worker worker = new Worker();
        worker.setName(data.getName());
        worker.setSurname(data.getSurname());
        worker.setJobDescription(data.getJobDescription());
        worker.setFilial(checkFilial);

        Worker savedWorker = workerRepository.save(worker);
        return new ApiResponse("Worker created successfully", true, savedWorker);
    }

    @Override
    public ApiResponse update(Long id, WorkerDTO data, User currentUser) throws Exception {
        Optional<Worker> optionalWorker = workerRepository.findById(id);

        if (!optionalWorker.isPresent()) {
            logger.info("Worker with id " + id + " does not exist");
            return new ApiResponse("Worker with id " + id + " does not exist", false);
        }

        Worker checkWorker = optionalWorker.get();
        Filial workerFilial = checkWorker.getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        if (currentUserFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            return new ApiResponse("Restricted: User does not have an assigned filial and is not an ADMIN", false);
        }

        if (currentUserFilial != null && !currentUserFilial.getId().equals(workerFilial.getId())) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
            return new ApiResponse("Restricted: User's assigned filial does not match the worker's filial", false);
        }

        Worker workerToUpdate = optionalWorker.get();
        FileEntity oldFileEntity = workerToUpdate.getFileEntity();

        if (data.getFileEntityId() != null) {
            Optional<FileEntity> newFileEntityOptional = fileRepository.findById(data.getFileEntityId());

            if (!newFileEntityOptional.isPresent()) {
                logger.info("FileEntity with id " + data.getFileEntityId() + " does not exist");
                return new ApiResponse("FileEntity with id " + data.getFileEntityId() + " does not exist", false);
            }

            FileEntity newFileEntity = newFileEntityOptional.get();
            workerToUpdate.setFileEntity(newFileEntity);
        } else if (oldFileEntity != null) {
            fileRepository.delete(oldFileEntity);
            workerToUpdate.setFileEntity(null);
        }

        workerToUpdate.setName(data.getName());
        workerToUpdate.setSurname(data.getSurname());
        workerToUpdate.setJobDescription(data.getJobDescription());

        Worker updatedWorker = workerRepository.save(workerToUpdate);
        return new ApiResponse("Worker updated successfully", true, updatedWorker);
    }

    @Override
    public Page<Worker> getAllByNameAndSurnameContains(String name, String surname, Pageable pageable) {
        return workerRepository.findAllByNameAndSurnameContains(name, surname, pageable);
    }

    @Override
    public Optional<Object> deleteById(Long id, User currentUser) {
        Optional<Worker> optionalWorker = workerRepository.findById(id);
        Worker checkWorker = optionalWorker.get();
        Filial workerFilial = checkWorker.getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUserFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            return Optional.empty();
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(workerFilial.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
            return Optional.empty();
        }
        if (!workerRepository.existsById(id)) {
            logger.info("Input with id " + id + " does not exists");
        }

        List<MonthlySalaryPayment> paymentsToDelete = monthlySalaryPaymentRepository.findAllByMonthlySalaryId(id);
        monthlySalaryPaymentRepository.deleteAll(paymentsToDelete);
        monthlySalaryRepository.deleteAll(monthlySalaryRepository.findAllByWorkerId(id));
        workerRepository.deleteById(id);
        return null;
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
