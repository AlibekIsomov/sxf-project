package com.sxf.project.repository;

import com.sxf.project.entity.MonthlySalaryPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Repository
public interface MonthlySalaryPaymentRepository extends JpaRepository<MonthlySalaryPayment, Long> {
    List<MonthlySalaryPayment> findByMonthlySalaryId(Long id);

    List<MonthlySalaryPayment> findAllByMonthlySalaryId(Long id);


    @Query("SELECT msp FROM MonthlySalaryPayment msp JOIN msp.monthlySalary ms JOIN ms.worker w WHERE w.filial.id = :filialId")
    List<MonthlySalaryPayment> findByFilialId(@Param("filialId") Long filialId);

    @Query("SELECT msp FROM MonthlySalaryPayment msp WHERE msp.createdAt BETWEEN :fromDate AND :toDate")
    List<MonthlySalaryPayment> findByDateRange(@Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);

    @Query("SELECT msp FROM MonthlySalaryPayment msp WHERE  msp.monthlySalary.worker.filial.id = :filialId AND msp.createdAt BETWEEN :fromDate AND :toDate")
    List<MonthlySalaryPayment> findByFilialIdAndDateRange(@Param("filialId") Long filialId, @Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);
}
