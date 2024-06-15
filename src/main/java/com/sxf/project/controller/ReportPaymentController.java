package com.sxf.project.controller;

import com.sxf.project.dto.ReportPaymentDTO;
import com.sxf.project.entity.ReportPayment;
import com.sxf.project.repository.ReportPaymentRepository;
import com.sxf.project.service.ReportPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/reportPayment")
public class ReportPaymentController {

    @Autowired
    ReportPaymentService paymentService;
    @Autowired
    ReportPaymentRepository paymentRepository;


    @GetMapping("/total/{reportId}")
    public ResponseEntity<Double> getTotalPaymentsByStore(@PathVariable Long reportId) {
        double totalPayments = paymentService.calculateTotalPaymentsByReport(reportId);
        return ResponseEntity.ok(totalPayments);
    }
//
//    @GetMapping("/release/{reportId}")
//    public ResponseEntity<Double> releasePaidAmount(
//            @PathVariable Long reportId,
//            @RequestParam int fullAmount)
//    {
//        double remainingAmount = paymentService.releasePaidAmount(reportId, fullAmount );
//        return ResponseEntity.ok(remainingAmount);
//    }


    @PostMapping("/{reportId}/add-payment")
    public ResponseEntity<ReportPayment> addPayment(
            @PathVariable Long reportId,
            @RequestParam Long newPayment) {
        return paymentService.addPayment(reportId, newPayment);
    }

    @PutMapping("/{reportId}/updatePayment/{paymentId}")
    public ResponseEntity<ReportPayment> updatePayment(
            @PathVariable Long reportId,
            @PathVariable Long paymentId,
            @RequestParam Long newPayment
    ) {
        return paymentService.updatePayment(reportId, paymentId, newPayment);
    }

    @GetMapping("/{reportId}/payments")
    public ResponseEntity<List<ReportPaymentDTO>> getAllPayments(@PathVariable Long reportId) {
        return paymentService.getAllPayments(reportId);
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<String> deletePayment(@PathVariable Long paymentId) {
        try {
            paymentService.deletePayment(paymentId);
            return new ResponseEntity<>("Payment deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            // Handle exceptions appropriately (e.g., log the error)
            return new ResponseEntity<>("Failed to delete payment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
