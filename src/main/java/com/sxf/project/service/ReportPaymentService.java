package com.sxf.project.service;

import com.sxf.project.dto.ReportDTO;
import com.sxf.project.dto.ReportPaymentDTO;
import com.sxf.project.entity.Report;
import com.sxf.project.entity.ReportPayment;
import com.sxf.project.entity.User;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReportPaymentService {


    ResponseEntity<ReportPayment> addPayment(Long reportId, Long newPayment, User currentUser);

    ResponseEntity<ReportPayment> updatePayment(Long reportId, Long reportPaymentId, Long newPayment, User currentUser);

    double calculateTotalPaymentsByReport(Long reportId);

    double releasePaidAmount(Long reportId) throws NotFoundException;


    void deletePayment(Long paymentId, User currentUser);

    ResponseEntity<List<ReportPaymentDTO>> getAllPayments(Long reportId, User currentUser);

    ReportDTO convertToDTO(Report report);
}
