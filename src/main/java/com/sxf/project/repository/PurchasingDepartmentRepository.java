package com.sxf.project.repository;

import com.sxf.project.entity.PurchasingDepartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasingDepartmentRepository extends JpaRepository<PurchasingDepartment, Long> {
    Page<PurchasingDepartment> findAllByName(String name, Pageable pageable);

    @Query("SELECT SUM(pd.number * pd.price) FROM PurchasingDepartment pd WHERE pd.profilePD.id = :profilePDId")
    Long calculateTotalFullAmountByProfilePD(@Param("profilePDId") Long profilePDId);

    @Query("SELECT pd.payment - SUM(pd.number * pd.price) FROM PurchasingDepartment pd WHERE pd.profilePD.id = :profilePDId GROUP BY pd.profilePD")
    Long calculateRemainingPaymentByProfilePD(@Param("profilePDId") Long profilePDId);;

    @Query("SELECT msp FROM PurchasingDepartment msp JOIN msp.profilePD  w WHERE w.filial.id = :filialId")
    List<PurchasingDepartment> findByFilialId(@Param("filialId") Long filialId);}

