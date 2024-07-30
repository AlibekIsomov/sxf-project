package com.sxf.project.controller;


import com.sxf.project.dto.PaymentStatisticDTO;
import com.sxf.project.dto.PaymentStatisticsResponseDTO;
import com.sxf.project.service.PaymentStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class PaymentStatisticController {

    @Autowired
    private PaymentStatisticService paymentService;

    @GetMapping("/filial/{filialId}")
    public ResponseEntity<List<PaymentStatisticDTO>> getPaymentsByFilialId(@PathVariable Long filialId) {
        List<PaymentStatisticDTO> payments = paymentService.getPaymentsByFilialId(filialId);
        if (payments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/income/{filialId}")
    public ResponseEntity<PaymentStatisticsResponseDTO> getIncome(@PathVariable Long filialId) {
        PaymentStatisticsResponseDTO income = paymentService.getIncomeByFilialId(filialId);
        return ResponseEntity.ok(income);
    }

    @GetMapping("/outcome/{filialId}")
    public ResponseEntity<PaymentStatisticsResponseDTO> getOutcome(@PathVariable Long filialId) {
        PaymentStatisticsResponseDTO outcome = paymentService.getOutcomeByFilialId(filialId);
        return ResponseEntity.ok(outcome);
    }

//
//    @GetMapping
//    public List<PaymentStatisticDTO> getPayments() {
//        return paymentService.getPayments();
//    }
//
//    @GetMapping("/income")
//    public PaymentStatisticsResponseDTO getAllIncome() {
//        return paymentService.getAllIncome();
//    }
//
//    @GetMapping("/outcome")
//    public PaymentStatisticsResponseDTO getAllOutcome() {
//        return paymentService.getAllOutcome();
//    }
    //

    @GetMapping("/paymentsByFilialId")
    public List<PaymentStatisticDTO> getPaymentsFromToDateByFilialId(
            @RequestParam("filialId") Long filialId,
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate) {
        return paymentService.getPaymentsByFilialIdFromToDate(filialId, fromDate, toDate);
    }

    @GetMapping("/incomeByFilialId")
    public PaymentStatisticsResponseDTO getAllIncomeFromToDateByFilialId(
            @RequestParam("filialId") Long filialId,
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate) {
        return paymentService.getAllIncomeByFilialIdFromToDate(filialId, fromDate, toDate);
    }

    @GetMapping("/outcomeByFilialId")
    public PaymentStatisticsResponseDTO getAllOutcomeFromToDateByFilialId(
            @RequestParam("filialId") Long filialId,
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate) {
        return paymentService.getAllIncomeByFilialIdFromToDate(filialId, fromDate, toDate);
    }

    @GetMapping
    public List<PaymentStatisticDTO> getPaymentsFromToDate(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate) {
        return paymentService.getPaymentsFromToDate(fromDate, toDate);
    }

    @GetMapping("/income")
    public PaymentStatisticsResponseDTO getAllIncomeFromToDate(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate) {
        return paymentService.getAllIncomeFromToDate(fromDate, toDate);
    }

    @GetMapping("/outcome")
    public PaymentStatisticsResponseDTO getAllOutcomFromToDatee(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate) {
        return paymentService.getAllOutcomeFromToDate(fromDate, toDate);
    }
}


