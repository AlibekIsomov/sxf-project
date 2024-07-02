package com.sxf.project.service;

import com.sxf.project.dto.MonthlySalaryDTO;
import com.sxf.project.dto.WorkerDTO;
import com.sxf.project.entity.MonthlySalary;
import com.sxf.project.entity.User;
import com.sxf.project.entity.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.Optional;

public interface WorkerService {

    Page<Worker> getAll(Pageable pageable) throws Exception;

    Optional<Worker> getById(Long id, User currentUser) throws Exception;

    Optional<Worker> create(WorkerDTO data, User currentUser) throws Exception;


    Optional<Worker> update(Long id, WorkerDTO data, User currentUser) throws Exception;

    Page<Worker> getAllByNameAndSurnameContains(String name, String surname, Pageable pageable);

    void deleteById(Long id, User currentUser);

    @Transactional
    Optional<MonthlySalary> createForSchedule(MonthlySalaryDTO data) throws Exception;
}
