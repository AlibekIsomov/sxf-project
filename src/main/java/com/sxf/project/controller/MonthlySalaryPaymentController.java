package com.sxf.project.controller;

import com.sxf.project.dto.MonthlySalaryPaymentDTO;
import com.sxf.project.entity.MonthlySalaryPayment;
import com.sxf.project.repository.MonthlySalaryPaymentRepository;
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
public class  MonthlySalaryPaymentController {

    @Autowired
    MonthlySalaryPaymentService monthlySalaryPaymentService;
    @Autowired
    MonthlySalaryPaymentRepository monthlySalaryPaymentRepository;

    @PostMapping
    public ResponseEntity<MonthlySalaryPayment> create(@RequestBody MonthlySalaryPaymentDTO data) throws Exception {
        try {
            Optional<MonthlySalaryPayment> createdMonthlySalary = monthlySalaryPaymentService.create(data);

            if (createdMonthlySalary.isPresent()) {
                return ResponseEntity.ok(createdMonthlySalary.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MonthlySalaryPayment> update(@PathVariable Long id, @RequestBody MonthlySalaryPaymentDTO data) {
        try {
            Optional<MonthlySalaryPayment> updatedMonthlySalary = monthlySalaryPaymentService.update(id, data);

            if (updatedMonthlySalary.isPresent()) {
                return ResponseEntity.ok(updatedMonthlySalary.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (NoSuchElementException storeNotFoundException) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        monthlySalaryPaymentService.deletePayment(id);
    }
    @GetMapping("/get/{monthlySalaryId}")
    public List<MonthlySalaryPayment> getMonthlySalariesByMonthlySalaryId(@PathVariable Long monthlySalaryId) {
        return monthlySalaryPaymentService.getMonthlySalariesByMonthlySalaryId(monthlySalaryId);
    }
}
