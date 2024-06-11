package com.sxf.project.repository;
import com.sxf.project.entity.MonthlySalaryPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlySalaryPaymentRepository extends JpaRepository<MonthlySalaryPayment, Long> {
    List<MonthlySalaryPayment> findByMonthlySalaryId(Long id);

    List<MonthlySalaryPayment> findAllByMonthlySalaryId(Long id);
}
