package com.sxf.project.repository;

import com.sxf.project.entity.ProfilePD;
import com.sxf.project.entity.PurchasingDepartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Repository
public interface PurchasingDepartmentRepository extends JpaRepository<PurchasingDepartment, Long> {
    Page<PurchasingDepartment> findAllByName(String name, Pageable pageable);

    @Query("SELECT GREATEST(COALESCE(SUM(pd.number * pd.price), 0), 0) FROM PurchasingDepartment pd WHERE pd.profilePD = :profilePD")
    Long calculateTotalFullAmountByProfilePD(@Param("profilePD") ProfilePD profilePD);

    @Query("SELECT msp FROM PurchasingDepartment msp JOIN msp.profilePD  w WHERE w.filial.id = :filialId")
    List<PurchasingDepartment> findByFilialId(@Param("filialId") Long filialId);

    List<PurchasingDepartment> findAllByProfilePDId(Long id);

    @Query("SELECT pd FROM PurchasingDepartment pd WHERE pd.createdAt BETWEEN :fromDate AND :toDate")
    List<PurchasingDepartment> findByDateRange(@Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);

    @Query("SELECT COALESCE(SUM(pd.payment), 0) FROM PurchasingDepartment pd WHERE pd.profilePD = :profilePD")
    Long calculateTotalPayment(@Param("profilePD") ProfilePD profilePD);

    @Query("SELECT pd FROM PurchasingDepartment pd  JOIN pd.profilePD w WHERE w.filial.id = :filialId AND pd.createdAt BETWEEN :fromDate AND :toDate")
    List<PurchasingDepartment> findByFilialIdAndDateRange(@Param("filialId") Long filialId, @Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);
}

