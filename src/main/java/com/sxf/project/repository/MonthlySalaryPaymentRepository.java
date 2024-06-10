package com.sxf.project.repository;
import com.sxf.project.entity.MonthlySalaryPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlySalaryPaymentRepository extends JpaRepository<MonthlySalaryPayment, Long> {
}
