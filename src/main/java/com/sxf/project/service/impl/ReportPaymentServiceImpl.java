package com.sxf.project.service.impl;

import com.sxf.project.dto.ReportDTO;
import com.sxf.project.dto.ReportPaymentDTO;
import com.sxf.project.entity.Report;
import com.sxf.project.entity.ReportPayment;
import com.sxf.project.repository.ReportPaymentRepository;
import com.sxf.project.repository.ReportRepository;
import com.sxf.project.service.ReportPaymentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportPaymentServiceImpl implements ReportPaymentService {

    @Autowired
    ReportPaymentRepository paymentRepository;

    @Autowired
    ReportRepository reportRepository;


    @Override
    public ResponseEntity<ReportPayment> addPayment(Long reportId, Long newPayment) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);

        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();

            // Check if the new payment is greater than or equal to the full amount
            if (newPayment > report.getPrice() || calculateTotalPaymentsByReport(reportId) >= report.getPrice()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }

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
    public ResponseEntity<ReportPayment> updatePayment(Long reportId, Long reportPaymentId, Long newPayment) {
        Optional<Report> reportOptional = reportRepository.findById(reportPaymentId);

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
    public void deletePayment(Long paymentId) {
        Optional<ReportPayment> paymentOptional = paymentRepository.findById(paymentId);

        if (paymentOptional.isPresent()) {
            ReportPayment paymentToDelete = paymentOptional.get();

            // Remove the payment from the associated store's payments list
            Report report = paymentToDelete.getReport();
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
    public ResponseEntity<List<ReportPaymentDTO>> getAllPayments(Long reportId){

        Optional<Report> reportOptional = reportRepository.findById(reportId);

        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();

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
}
