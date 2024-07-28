package com.sxf.project.service;

import com.sxf.project.dto.MonthlySalaryDTO;
import com.sxf.project.dto.WorkerDTO;
import com.sxf.project.entity.MonthlySalary;
import com.sxf.project.entity.User;
import com.sxf.project.entity.Worker;
import com.sxf.project.payload.ApiResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WorkerService {

    Page<Worker> getAll(Pageable pageable) throws Exception;

    List<Worker> getAllByFilial(Long id);

    List<Worker> getWorkerByFilial(User user);

    Optional<Worker> getById(Long id, User currentUser) throws Exception;


    ApiResponse create(WorkerDTO data, User currentUser) throws Exception;

    ApiResponse update(Long id, WorkerDTO data, User currentUser) throws Exception;

    Page<Worker> getAllByNameAndSurnameContains(String name, String surname, Pageable pageable);

    Optional<Object> deleteById(Long id, User currentUser);

    @Transactional
    Optional<MonthlySalary> createForSchedule(MonthlySalaryDTO data) throws Exception;
}
