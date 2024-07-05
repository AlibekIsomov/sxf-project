package com.sxf.project.service.impl;

import com.sxf.project.dto.MonthlySalaryDTO;
import com.sxf.project.entity.*;
import com.sxf.project.repository.MonthlySalaryPaymentRepository;
import com.sxf.project.repository.MonthlySalaryRepository;
import com.sxf.project.repository.WorkerRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.MonthlySalaryService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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
    public Optional<MonthlySalary> create(MonthlySalaryDTO data, @CurrentUser User currentUser) throws Exception {

        Optional<Worker> workerOptional = workerRepository.findById(data.getWorkerId());

        Worker worker = workerOptional.get();
        Filial Filialcheck = workerOptional.get().getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUserFilial == null && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            return Optional.empty();
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(Filialcheck.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
            return Optional.empty();
        }
            if (workerOptional.isPresent()) {
                 return Optional.empty();
            }
            else {
                logger.info("Such ID worker does not exist!");
            }

            worker.setCurrentSalary(data.getPaymentAmount());
            workerRepository.save(worker);
            MonthlySalary monthlySalary = new MonthlySalary();

            monthlySalary.setMonthDate(data.getMonthDate());
            monthlySalary.setStatus(PaymentStatus.valueOf(data.getStatus()));
            monthlySalary.setPaymentAmount(data.getPaymentAmount());
            monthlySalary.setPaidAmount(data.getPaidAmount());
            monthlySalary.setWorker(workerOptional.get());

        return Optional.of(monthlySalaryRepository.save(monthlySalary));

    }

    @Override
    public Optional<MonthlySalary> update(Long id, MonthlySalaryDTO data, User currentUser) throws Exception {

        Optional<MonthlySalary> optionalMonthlySalary = monthlySalaryRepository.findById(id);

        if (!optionalMonthlySalary.isPresent()) {
            logger.info("Such ID MonthlySalaryPayments does not exist!");
        }

        Optional<Worker> workerOptional = workerRepository.findById(data.getWorkerId());

        Worker checkFilial = workerOptional.get();

        Filial Filialcheck = workerOptional.get().getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUserFilial == null && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            return Optional.empty();
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(Filialcheck.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
            return Optional.empty();
        }

        if (workerOptional.isPresent()) {
            Worker worker = workerOptional.get();
            worker.setCurrentSalary(data.getPaymentAmount());
            workerRepository.save(worker);
        } else {
            logger.info("Such ID worker does not exist!");
        }


            MonthlySalary monthlySalary = optionalMonthlySalary.get();

            monthlySalary.setMonthDate(data.getMonthDate());
            monthlySalary.setStatus(PaymentStatus.valueOf(data.getStatus()));
            monthlySalary.setPaymentAmount(data.getPaymentAmount());
            monthlySalary.setPaidAmount(data.getPaidAmount());
            monthlySalary.setWorker(workerOptional.get());

        return Optional.of(monthlySalaryRepository.save(monthlySalary));

    }

    @Override
    public void deleteById(Long id, User currentUser) {
        Optional<MonthlySalary> optionalMonthlySalary = monthlySalaryRepository.findById(id);
        MonthlySalary checkFilial = optionalMonthlySalary.get();

        Filial workerFilial = checkFilial.getWorker().getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUserFilial == null && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(workerFilial.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
        }

        if(!optionalMonthlySalary.isPresent()) {
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
        if (currentUserFilial == null && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(workerFilial.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
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
        if (currentUserFilial == null && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(workerFilial.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
        }

        if (!monthlySalaryRepository.existsById(id)) {
            logger.info("Input with id " + id + " does not exists");
            return Optional.empty();
        }
        return monthlySalaryRepository.findById(id);
    }



}
