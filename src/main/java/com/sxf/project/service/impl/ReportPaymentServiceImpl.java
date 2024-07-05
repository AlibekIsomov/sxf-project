package com.sxf.project.service.impl;

import com.sxf.project.dto.ReportDTO;
import com.sxf.project.dto.ReportPaymentDTO;
import com.sxf.project.entity.*;
import com.sxf.project.repository.ReportPaymentRepository;
import com.sxf.project.repository.ReportRepository;
import com.sxf.project.service.ReportPaymentService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
public class ReportPaymentServiceImpl implements ReportPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(ReportPaymentServiceImpl.class);

    @Autowired
    ReportPaymentRepository paymentRepository;

    @Autowired
    ReportRepository reportRepository;


    @Override
    public ResponseEntity<ReportPayment> addPayment(Long reportId, Long newPayment, User currentUser) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);
        Report report = reportOptional.get();

        Filial Filialcheck = report.getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUserFilial == null && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            return ResponseEntity.status(403).body(null);
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(Filialcheck.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
            return ResponseEntity.status(403).body(null);
        }
        if (reportOptional.isPresent()) {


            ReportPayment payment = new ReportPayment();
            payment.setNewPayment(newPayment);
            payment.setReport(report);

            report.getPayments().add(payment);

            // report.setLastPayment(newPayment);

            reportRepository.save(report);

            return ResponseEntity.ok(payment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public ResponseEntity<ReportPayment> updatePayment(Long reportId, Long reportPaymentId, Long newPayment, User currentUser) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);
        Filial Filialcheck = reportOptional.get().getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUserFilial == null && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            return ResponseEntity.status(403).body(null);
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(Filialcheck.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
            return ResponseEntity.status(403).body(null);
        }

        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            // Find the existing payment by ID
            Optional<ReportPayment> paymentOptional = report.getPayments().stream()
                    .filter(payment -> payment.getId().equals(reportPaymentId))
                    .findFirst();

            if (paymentOptional.isPresent()) {
                ReportPayment existingPayment = paymentOptional.get();

                // Update the existing payment
                existingPayment.setNewPayment(newPayment);

                // Update the store's lastPayment to the new payment
//                report.setLastPayment(newPayment);

                // Save the updated store (including the updated payment)
                reportRepository.save(report);

                // Convert and return the updated store as a DTO
                return ResponseEntity.ok(existingPayment);
            } else {
                // Handle the case where the specified payment ID is not found
                return ResponseEntity.notFound().build();
            }
        } else {
            // Handle the case where the specified store ID is not found
            return ResponseEntity.notFound().build();
        }
    }

        @Override
        public double calculateTotalPaymentsByReport(Long reportId){
            Report report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new EntityNotFoundException("Report not found with id: " + reportId));

            return paymentRepository.calculateTotalPaymentsByReport(report);
        }

        @Override
        public double releasePaidAmount(Long reportId) {
        // Fetch the report from the database (assuming a Report entity exists)
             Report report = paymentRepository.findById(reportId)
                .orElseThrow(() ->  new EntityNotFoundException("Report not found for id: " + reportId)).getReport();

        // Business logic to calculate the remaining amount
            double totalAmount = report.getPrice();
            double paidAmount = calculateTotalPaymentsByReport(reportId);


            return totalAmount - paidAmount;
    }




    @Override
    public void deletePayment(Long paymentId, User currentUser) {
        Optional<ReportPayment> paymentOptional = paymentRepository.findById(paymentId);

        if (paymentOptional.isPresent()) {
            ReportPayment paymentToDelete = paymentOptional.get();

            // Remove the payment from the associated store's payments list
            Report report = paymentToDelete.getReport();

            Filial Filialcheck = report.getFilial();
            Filial currentUserFilial = currentUser.getAssignedFilial();

            // Check if the current user is not assigned to a filial and is not an admin
            if (currentUserFilial == null && !currentUser.getRoles().contains(Role.ADMIN)) {
                logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            }

            // If the current user has an assigned filial, check if it matches the worker's filial
            if (currentUserFilial != null && !currentUserFilial.getId().equals(Filialcheck.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
                logger.info("Restricted: User's assigned filial does not match the worker's filial");
            }

            if (report != null) {
                report.getPayments().remove(paymentToDelete);
            }

            // Delete the payment from the database
            paymentRepository.delete(paymentToDelete);
        } else {
            // Handle the case where the payment with the given id is not found
            // You can throw an exception, log a message, or handle it in another way.
            // For simplicity, I'll log a message.
            System.out.println("Payment with id " + paymentId + " not found");
        }
    }

    @Override
    public ResponseEntity<List<ReportPaymentDTO>> getAllPayments(Long reportId, User currentUser){

        Optional<Report> reportOptional = reportRepository.findById(reportId);

        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();

            if (!report.getFilial().getId().equals(currentUser.getAssignedFilial().getId()) &&
                    !currentUser.getRoles().contains(Role.ADMIN)) {
                throw new AccessDeniedException("Restricted for this manager");
            }
            List<ReportPaymentDTO> paymentDTOs = report.getPayments()
                    .stream()
                    .map(this::convertToPaymentDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(paymentDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ReportDTO convertToDTO(Report report) {
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId(report.getId());
        reportDTO.setPrice(report.getPrice());

        List<ReportPaymentDTO> reportPaymentDTOs = report.getPayments()
                .stream()
                .map(this::convertToPaymentDTO)
                .collect(Collectors.toList());
        reportDTO.setPayments(reportPaymentDTOs);

        return reportDTO;
    }


    private ReportPaymentDTO convertToPaymentDTO(ReportPayment payments) {
        ReportPaymentDTO paymentDTO = new ReportPaymentDTO();
        paymentDTO.setId(payments.getId());
        paymentDTO.setNewPayment(payments.getNewPayment());
        paymentDTO.setCreatedAt(payments.getCreatedAt());
        return paymentDTO;


    }

    public class ReportNotFoundException extends RuntimeException {
        public ReportNotFoundException(String message) {
            super(message);
        }
    }

}
