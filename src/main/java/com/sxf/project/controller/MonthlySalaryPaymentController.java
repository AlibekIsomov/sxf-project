package com.sxf.project.controller;

import com.sxf.project.dto.MonthlySalaryPaymentDTO;
import com.sxf.project.entity.MonthlySalaryPayment;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.repository.MonthlySalaryPaymentRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.MonthlySalaryPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/monthlySalaryPayment")
public class MonthlySalaryPaymentController {

    @Autowired
    MonthlySalaryPaymentService monthlySalaryPaymentService;
    @Autowired
    MonthlySalaryPaymentRepository monthlySalaryPaymentRepository;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MonthlySalaryPaymentDTO data, @CurrentUser User currentUser) throws Exception {
            ApiResponse apiResponse = monthlySalaryPaymentService.create(data, currentUser);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MonthlySalaryPaymentDTO data, @CurrentUser User currentUser) throws Exception {
           ApiResponse apiResponse = monthlySalaryPaymentService.update(id, data, currentUser);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id, @CurrentUser User currentUser) throws Exception {
        monthlySalaryPaymentService.deletePayment(id, currentUser);
    }

    @GetMapping("/get/{monthlySalaryId}")
    public List<MonthlySalaryPayment> getMonthlySalariesByMonthlySalaryId(@PathVariable Long monthlySalaryId, @CurrentUser User currentUser) throws Exception {
        return monthlySalaryPaymentService.getMonthlySalariesByMonthlySalaryId(monthlySalaryId, currentUser);
    }
}
