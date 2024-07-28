package com.sxf.project.service.impl;

import com.sxf.project.dto.MonthlySalaryDTO;
import com.sxf.project.entity.*;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.repository.MonthlySalaryPaymentRepository;
import com.sxf.project.repository.MonthlySalaryRepository;
import com.sxf.project.repository.WorkerRepository;
import com.sxf.project.service.MonthlySalaryService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MonthlySalaryServiceImpl implements MonthlySalaryService {
    private static final Logger logger = LoggerFactory.getLogger(MonthlySalaryServiceImpl.class);

    @Autowired
    MonthlySalaryRepository monthlySalaryRepository;

    @Autowired
    WorkerRepository workerRepository;

    @Autowired
    MonthlySalaryPaymentRepository monthlySalaryPaymentRepository;


    @Override
    public ApiResponse create(MonthlySalaryDTO data, User currentUser) throws Exception {
        Optional<Worker> workerOptional = workerRepository.findById(data.getWorkerId());

        if (!workerOptional.isPresent()) {
            logger.info("Such ID worker does not exist!");
            return new ApiResponse("Bunaqa Idlik worker yo'q!", false);
        }

        Worker worker = workerOptional.get();
        Filial workerFilial = worker.getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        if (isUserRestricted(currentUser, currentUserFilial, workerFilial)) {
            return new ApiResponse("Siz uchun hech qanday filial ulanmagan!", false);
        }

        worker.setCurrentSalary(data.getPaymentAmount());
        workerRepository.save(worker);

        MonthlySalary monthlySalary = new MonthlySalary();
        monthlySalary.setMonthDate(data.getMonthDate());
        monthlySalary.setStatus(PaymentStatus.valueOf(data.getStatus()));
        monthlySalary.setPaymentAmount(data.getPaymentAmount());
        monthlySalary.setPaidAmount(data.getPaidAmount());
        monthlySalary.setWorker(worker);

        MonthlySalary savedMonthlySalary = monthlySalaryRepository.save(monthlySalary);
        return new ApiResponse("MonthlySalary muvaffaqiyatli yaratildi!", true, savedMonthlySalary);
    }

    @Override
    public ApiResponse update(Long id, MonthlySalaryDTO data, User currentUser) throws Exception {
        Optional<MonthlySalary> optionalMonthlySalary = monthlySalaryRepository.findById(id);

        if (!optionalMonthlySalary.isPresent()) {
            logger.info("Such ID MonthlySalary does not exist!");
            return new ApiResponse("Bunaqa Idlik MonthlySalary yo'q!", false);
        }

        Optional<Worker> workerOptional = workerRepository.findById(data.getWorkerId());

        if (!workerOptional.isPresent()) {
            logger.info("Such ID worker does not exist!");
            return new ApiResponse("Bunaqa Idlik worker yo'q!", false);
        }

        Worker worker = workerOptional.get();
        Filial workerFilial = worker.getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        if (isUserRestricted(currentUser, currentUserFilial, workerFilial)) {
            return new ApiResponse("Siz uchun hech qanday filial ulanmagan!", false);
        }

        worker.setCurrentSalary(data.getPaymentAmount());
        workerRepository.save(worker);

        MonthlySalary monthlySalary = optionalMonthlySalary.get();
        monthlySalary.setMonthDate(data.getMonthDate());
        monthlySalary.setStatus(PaymentStatus.valueOf(data.getStatus()));
        monthlySalary.setPaymentAmount(data.getPaymentAmount());
        monthlySalary.setPaidAmount(data.getPaidAmount());
        monthlySalary.setWorker(worker);

        MonthlySalary updatedMonthlySalary = monthlySalaryRepository.save(monthlySalary);
        return new ApiResponse("MonthlySalary muvaffaqiyatli yangilandi!", true, updatedMonthlySalary);
    }

    private boolean isUserRestricted(User currentUser, Filial currentUserFilial, Filial checkFilial) {
        if (currentUserFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            return true;
        }

        if (currentUserFilial != null && !currentUserFilial.getId().equals(checkFilial.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the checkFilial");
            return true;
        }

        return false;
    }

    @Override
    public void deleteById(Long id, User currentUser) {
        Optional<MonthlySalary> optionalMonthlySalary = monthlySalaryRepository.findById(id);
        MonthlySalary checkFilial = optionalMonthlySalary.get();

        Filial workerFilial = checkFilial.getWorker().getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUserFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(workerFilial.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
        }

        if (!optionalMonthlySalary.isPresent()) {
            logger.info("Store with id " + id + " does not exists");
        }

        List<MonthlySalaryPayment> paymentsToDelete = monthlySalaryPaymentRepository.findAllByMonthlySalaryId(id);
        monthlySalaryPaymentRepository.deleteAll(paymentsToDelete);

        monthlySalaryRepository.deleteById(id);
    }



    @Override
    public Page<MonthlySalary> getAll(Pageable pageable) throws Exception {
        return monthlySalaryRepository.findAll(pageable);
    }

    @Override
    public List<MonthlySalary> getMonthlySalariesByWorkerId(Long workerId, User currentUser) {
        Optional<MonthlySalary> optionalMonthlySalary = monthlySalaryRepository.findById(workerId);

        MonthlySalary checkFilial = optionalMonthlySalary.get();

        Filial workerFilial = checkFilial.getWorker().getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUserFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(workerFilial.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
        }

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new EntityNotFoundException("Worker not found with id: " + workerId));

        return monthlySalaryRepository.findByWorkerId(worker.getId());
    }


    @Override
    public Optional<MonthlySalary> getById(Long id, User currentUser) throws Exception {
        Optional<MonthlySalary> optionalMonthlySalary = monthlySalaryRepository.findById(id);

        MonthlySalary checkFilial = optionalMonthlySalary.get();

        Filial workerFilial = checkFilial.getWorker().getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUserFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(workerFilial.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
        }

        if (!monthlySalaryRepository.existsById(id)) {
            logger.info("Input with id " + id + " does not exists");
            return Optional.empty();
        }
        return monthlySalaryRepository.findById(id);
    }


}
