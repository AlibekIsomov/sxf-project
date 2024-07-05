package com.sxf.project.service.impl;

import com.sxf.project.dto.MonthlySalaryPaymentDTO;
import com.sxf.project.entity.MonthlySalary;
import com.sxf.project.entity.MonthlySalaryPayment;
import com.sxf.project.entity.Role;
import com.sxf.project.entity.User;
import com.sxf.project.repository.MonthlySalaryPaymentRepository;
import com.sxf.project.repository.MonthlySalaryRepository;
import com.sxf.project.service.MonthlySalaryPaymentService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MonthlySalaryPaymentServiceImpl implements MonthlySalaryPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(MonthlySalaryPaymentServiceImpl.class);

    @Autowired
    MonthlySalaryPaymentRepository monthlySalaryPaymentRepository;

    @Autowired
    MonthlySalaryRepository monthlySalaryRepository;


    @Override
    public Optional<MonthlySalaryPayment> create(MonthlySalaryPaymentDTO data, User currentUser) throws Exception {

        Optional<MonthlySalary> monthlySalaryOptional = monthlySalaryRepository.findById(data.getMonthlySalaryId());
        MonthlySalary checkFilial = monthlySalaryOptional.get();

        if (!checkFilial.getWorker().getFilial().getId().equals(currentUser.getAssignedFilial().getId()) &&
                !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new AccessDeniedException("Restricted for this manager");
        }

        if (!monthlySalaryOptional.isPresent()) {

            logger.info("Such ID category does not exist!");
        }

        MonthlySalaryPayment monthlySalaryPayment = new MonthlySalaryPayment();
        monthlySalaryPayment.setPaymentAmount(data.getPaymentAmount());
        monthlySalaryPayment.setMonthlySalary(monthlySalaryOptional.get());

        return Optional.of(monthlySalaryPaymentRepository.save(monthlySalaryPayment));
    }


    @Override
    public Optional<MonthlySalaryPayment> update(Long id, MonthlySalaryPaymentDTO data, User currentUser) throws Exception {

        Optional<MonthlySalaryPayment> optionalMonthlySalaryPayments = monthlySalaryPaymentRepository.findById(id);

        if (!optionalMonthlySalaryPayments.isPresent()) {
            logger.info("Such ID MonthlySalaryPayments does not exist!");
        }

        Optional<MonthlySalary> monthlySalaryOptional = monthlySalaryRepository.findById(data.getMonthlySalaryId());
        MonthlySalary checkFilial = monthlySalaryOptional.get();
        if (!checkFilial.getWorker().getFilial().getId().equals(currentUser.getAssignedFilial().getId()) &&
                !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new AccessDeniedException("Restricted for this manager");
        }
        if (!monthlySalaryOptional.isPresent()) {
            logger.info("Such ID monthlySalary does not exist!");
        }

        MonthlySalaryPayment monthlySalaryPayment = optionalMonthlySalaryPayments.get();

        monthlySalaryPayment.setPaymentAmount(data.getPaymentAmount());
        monthlySalaryPayment.setMonthlySalary(monthlySalaryOptional.get());



        return Optional.of(monthlySalaryPaymentRepository.save(monthlySalaryPayment));
    }

    @Override
    public void deletePayment(Long monthlySalaryPaymentsId, User currentUser) {
        Optional<MonthlySalaryPayment> paymentOptional = monthlySalaryPaymentRepository.findById(monthlySalaryPaymentsId);
        MonthlySalaryPayment checkFilial = paymentOptional.get();
        if (!checkFilial.getMonthlySalary().getWorker().getFilial().getId().equals(currentUser.getAssignedFilial().getId()) &&
                !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new AccessDeniedException("Restricted for this manager");
        }
        if (paymentOptional.isPresent()) {
            MonthlySalaryPayment paymentToDelete = paymentOptional.get();

            monthlySalaryPaymentRepository.delete(paymentToDelete);
        } else {

            System.out.println("Payment with id " + monthlySalaryPaymentsId + " not found");
        }
    }

    @Override
    public List<MonthlySalaryPayment> getMonthlySalariesByMonthlySalaryId(Long monthlySalaryId, User currentUser) {
        MonthlySalary monthlySalary = monthlySalaryRepository.findById(monthlySalaryId)
                .orElseThrow(() -> new EntityNotFoundException("Worker not found with id: " + monthlySalaryId));

        Optional<MonthlySalary> monthlySalaryOptional = monthlySalaryRepository.findById(monthlySalaryId);
        MonthlySalary checkFilial = monthlySalaryOptional.get();

        if (!checkFilial.getWorker().getFilial().getId().equals(currentUser.getAssignedFilial().getId()) &&
                !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new AccessDeniedException("Restricted for this manager");
        }
        return monthlySalaryPaymentRepository.findByMonthlySalaryId(monthlySalary.getId());
    }
}
