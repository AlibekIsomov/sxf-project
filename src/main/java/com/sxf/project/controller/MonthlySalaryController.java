package com.sxf.project.controller;


import com.sxf.project.dto.MonthlySalaryDTO;
import com.sxf.project.entity.MonthlySalary;
import com.sxf.project.entity.User;
import com.sxf.project.repository.MonthlySalaryRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.MonthlySalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/monthlySalary")
public class MonthlySalaryController {

    @Autowired
    MonthlySalaryService monthlySalaryService;
    @Autowired
    MonthlySalaryRepository monthlySalaryRepository;

    @GetMapping
    public ResponseEntity<Page<MonthlySalary>> getAll(Pageable pageable) throws Exception {
        return ResponseEntity.ok(monthlySalaryService.getAll(pageable));
    }


    @GetMapping("/{id}")
    public ResponseEntity<MonthlySalary> getById(@PathVariable Long id,
                                                 @CurrentUser User currentUser) throws Exception {
        return monthlySalaryService.getById(id, currentUser).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MonthlySalary> create(@RequestBody MonthlySalaryDTO data,
                                                @CurrentUser User currentUser) throws Exception {
        try {
            Optional<MonthlySalary> createdMonthlySalary = monthlySalaryService.create(data, currentUser);

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
    public ResponseEntity<MonthlySalary> update(@PathVariable Long id,
                                                @RequestBody MonthlySalaryDTO data,
                                                @CurrentUser User currentUser) throws Exception {
        try {
            Optional<MonthlySalary> updatedMonthlySalary = monthlySalaryService.update(id, data, currentUser);

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
    public void deleteById(@PathVariable Long id,
                           @CurrentUser User currentUser) throws Exception {
        monthlySalaryService.deleteById(id, currentUser);
    }


    @GetMapping("/worker/{workerId}")
    public List<MonthlySalary> getMonthlySalariesByWorkerId(@PathVariable Long workerId,
                                                            @CurrentUser User currentUser) throws Exception {
        return monthlySalaryService.getMonthlySalariesByWorkerId(workerId, currentUser);
    }
}
