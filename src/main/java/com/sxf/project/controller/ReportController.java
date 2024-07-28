package com.sxf.project.controller;


import com.sxf.project.dto.ReportDTO;
import com.sxf.project.entity.Report;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.repository.ReportRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.ReportService;
import jakarta.transaction.Transactional;
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
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    ReportService reportService;
    @Autowired
    ReportRepository reportRepository;

    @Transactional
    @GetMapping
    public ResponseEntity<Page<Report>> getAll(Pageable pageable) throws Exception {
        return ResponseEntity.ok(reportService.getAll(pageable));
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<Report> getById(@PathVariable Long id, @CurrentUser User currentUser) throws Exception {
        return reportService.getById(id, currentUser).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ReportDTO data, @CurrentUser User currentUser) throws Exception {
            ApiResponse apiResponse = reportService.create(data, currentUser);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/filial")
    public ResponseEntity<?> getWorkersByFilial(@CurrentUser User currentUser) {
        List<Report> reports = reportService.getReportByFilial(currentUser);
        if (reports.isEmpty()) {
            return ResponseEntity.badRequest().body("Siz uchun hech qanday filial bog'lanmagan!");
        }

        return ResponseEntity.ok(reports);
    }

    @GetMapping("/filial/{id}")
    public ResponseEntity<?> getWorkersByFilial(@PathVariable Long id) {
        List<Report> Report = reportService.getAllByFilial(id);
        if (Report.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bunaqa ID lik filal yo'q");
        }
        return ResponseEntity.ok(Report);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ReportDTO data, @CurrentUser User currentUser) throws Exception {
            ApiResponse apiResponse = reportService.update(id, data, currentUser);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id, @CurrentUser User currentUser) {
        try {
            reportService.deleteById(id, currentUser);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Siz uchun emas");
        }
    }
}
