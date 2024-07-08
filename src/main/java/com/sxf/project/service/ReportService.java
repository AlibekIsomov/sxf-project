package com.sxf.project.service;

import com.sxf.project.dto.ReportDTO;
import com.sxf.project.entity.Report;
import com.sxf.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReportService {

    Page<Report> getAll(Pageable pageable) throws Exception;


    List<Report> getReportByFilial(User user);

    Optional<Report> getById(Long id, User currentUser) throws Exception;

    List<Report> getAllByFilial(Long id);

    Optional<Report> create(ReportDTO data, User currentUser) throws Exception;

    Optional<Report> update(Long id, ReportDTO data, User currentUser) throws Exception;

    void deleteById(Long id, User currentUser);
}
