package com.sxf.project.service;

import com.sxf.project.dto.MonthlySalaryDTO;
import com.sxf.project.dto.WorkerDTO;
import com.sxf.project.entity.MonthlySalary;
import com.sxf.project.entity.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.Optional;

public interface WorkerService {
    Page<Worker> getAll(Pageable pageable) throws Exception;

    Optional<Worker> getById(Long id) throws Exception;

    Optional<Worker> create(WorkerDTO data) throws Exception;

    Optional<Worker> update(Long id, WorkerDTO data) throws Exception;


    Page<Worker> getAllByNameAndSurnameContains(String name, String surname, Pageable pageable);

    void deleteById(Long id);

    @Transactional
    Optional<MonthlySalary> createForSchedule(MonthlySalaryDTO data) throws Exception;
}
