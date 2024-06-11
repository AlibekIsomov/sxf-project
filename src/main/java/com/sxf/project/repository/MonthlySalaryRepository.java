package com.sxf.project.repository;

import com.sxf.project.entity.MonthlySalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlySalaryRepository extends JpaRepository<MonthlySalary, Long> {
    List<MonthlySalary> findByWorkerId(Long id);

    Iterable<? extends MonthlySalary> findAllByWorkerId(Long id);
}
