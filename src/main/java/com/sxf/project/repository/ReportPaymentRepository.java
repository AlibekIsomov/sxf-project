package com.sxf.project.repository;

import com.sxf.project.entity.ReportPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPaymentRepository extends JpaRepository<ReportPayment, Long> {
}
