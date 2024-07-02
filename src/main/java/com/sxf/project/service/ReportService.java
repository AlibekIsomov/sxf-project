package com.sxf.project.service;

import com.sxf.project.dto.ReportDTO;
import com.sxf.project.entity.Report;
import com.sxf.project.entity.ReportPayment;
import com.sxf.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ReportService {

    Page<Report> getAll(Pageable pageable) throws Exception;


    Optional<Report> getById(Long id, User currentUser) throws Exception;

    Optional<Report> create(ReportDTO data, User currentUser) throws Exception;

    Optional<Report> update(Long id, ReportDTO data, User currentUser) throws Exception;

    void deleteById(Long id, User currentUser);
}
