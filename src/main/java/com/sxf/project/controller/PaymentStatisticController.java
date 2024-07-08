package com.sxf.project.controller;


import com.sxf.project.dto.PaymentStatisticDTO;
import com.sxf.project.service.PaymentStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
