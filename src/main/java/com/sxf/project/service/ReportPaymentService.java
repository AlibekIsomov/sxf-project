package com.sxf.project.service;

import com.sxf.project.dto.ReportDTO;
import com.sxf.project.dto.ReportPaymentDTO;
import com.sxf.project.entity.Report;
import com.sxf.project.entity.ReportPayment;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReportPaymentService {
    ResponseEntity<ReportPayment> addPayment(Long reportId, Long newPayment);

    ResponseEntity<ReportPayment> updatePayment(Long reportId, Long paymentId, Long newPayment);

    double calculateTotalPaymentsByReport(Long reportId);

    void deletePayment(Long paymentId);

    ResponseEntity<List<ReportPaymentDTO>> getAllPayments(Long reportId);

    ReportDTO convertToDTO(Report report);
}
