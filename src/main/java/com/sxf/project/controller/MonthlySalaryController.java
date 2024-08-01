package com.sxf.project.controller;


import com.sxf.project.dto.MonthlySalaryDTO;
import com.sxf.project.entity.MonthlySalary;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.repository.MonthlySalaryRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.MonthlySalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> create(@RequestBody MonthlySalaryDTO data,
                                                @CurrentUser User currentUser) throws Exception {
        ApiResponse apiResponse = monthlySalaryService.create( data, currentUser);

        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
        }


    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                                @RequestBody MonthlySalaryDTO data,
                                                @CurrentUser User currentUser) throws Exception {
        ApiResponse apiResponse = monthlySalaryService.update(id, data, currentUser);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);

    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id,
                           @CurrentUser User currentUser) throws Exception {
        monthlySalaryService.deleteById(id, currentUser);
    }


    @GetMapping("/worker/{workerId}")
    public List<MonthlySalary> getMonthlySalariesByWorkerId(@PathVariable Long workerId,
                                                            @CurrentUser User currentUser) {
        return monthlySalaryService.getMonthlySalariesByWorkerId(workerId, currentUser);
    }
}
