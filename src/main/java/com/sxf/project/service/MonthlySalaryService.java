package com.sxf.project.service;

import com.sxf.project.dto.MonthlySalaryDTO;
import com.sxf.project.entity.MonthlySalary;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MonthlySalaryService {


    ApiResponse create(MonthlySalaryDTO data, User currentUser) throws Exception;

    ApiResponse update(Long id, MonthlySalaryDTO data, User currentUser) throws Exception;

    void deleteById(Long id, User currentUser);

    Page<MonthlySalary> getAll(Pageable pageable) throws Exception;

    List<MonthlySalary> getMonthlySalariesByWorkerId(Long workerId, User currentUser);

    Optional<MonthlySalary> getById(Long id, User currentUser) throws Exception;
}
