package com.sxf.project.service;

import com.sxf.project.dto.MonthlySalaryDTO;
import com.sxf.project.entity.MonthlySalary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MonthlySalaryService {
    Optional<MonthlySalary> create(MonthlySalaryDTO data) throws Exception;

    Optional<MonthlySalary> update(Long id, MonthlySalaryDTO data) throws Exception;

    void deleteById(Long id);

    Page<MonthlySalary> getAll(Pageable pageable) throws Exception;

    Optional<MonthlySalary> getById(Long id) throws Exception;
}
