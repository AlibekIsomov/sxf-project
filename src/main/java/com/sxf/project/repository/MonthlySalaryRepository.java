package com.sxf.project.repository;

import com.sxf.project.entity.MonthlySalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlySalaryRepository extends JpaRepository<MonthlySalary, Long> {
}
