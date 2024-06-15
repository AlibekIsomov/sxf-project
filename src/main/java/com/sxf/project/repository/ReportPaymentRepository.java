package com.sxf.project.repository;

import com.sxf.project.entity.Report;
import com.sxf.project.entity.ReportPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPaymentRepository extends JpaRepository<ReportPayment, Long> {
    @Query("SELECT COALESCE(SUM(p.newPayment), 0) FROM ReportPayment p WHERE p.report = :report")
    Long calculateTotalPaymentsByReport(@Param("report") Report report);

}
