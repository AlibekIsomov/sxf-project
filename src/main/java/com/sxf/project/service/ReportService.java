package com.sxf.project.service;

import com.sxf.project.dto.ReportDTO;
import com.sxf.project.entity.Report;
import com.sxf.project.entity.ReportPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ReportService {

    Page<Report> getAll(Pageable pageable) throws Exception;

    Optional<Report> getById(Long id) throws Exception;

    Optional<Report> create(ReportDTO data) throws Exception;

    Optional<Report> update(Long id, ReportDTO data) throws Exception;

    void deleteById(Long id);
}
