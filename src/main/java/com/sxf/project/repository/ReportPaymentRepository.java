package com.sxf.project.repository;

import com.sxf.project.entity.Report;
import com.sxf.project.entity.ReportPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReportPaymentRepository extends JpaRepository<ReportPayment, Long> {
    @Query("SELECT COALESCE(SUM(p.newPayment), 0) FROM ReportPayment p WHERE p.report = :report")
    Long calculateTotalPaymentsByReport(@Param("report") Report report);

//    List<ReportPayment> findByFilialId(Long filialId);

    @Query("SELECT msp FROM ReportPayment msp JOIN msp.report w WHERE w.filial.id = :filialId")
    List<ReportPayment> findByFilialId(@Param("filialId") Long filialId);

    @Query("SELECT rp FROM ReportPayment rp WHERE rp.createdAt BETWEEN :fromDate AND :toDate")
    List<ReportPayment> findByDateRange(@Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);

    @Query("SELECT rp FROM ReportPayment rp JOIN rp.report r WHERE r.filial.id = :filialId AND rp.createdAt BETWEEN :fromDate AND :toDate")
    List<ReportPayment> findByFilialIdAndDateRange(@Param("filialId") Long filialId, @Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);

}
