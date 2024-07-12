package com.sxf.project.repository;

import com.sxf.project.entity.ProfilePD;
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

    @Query("SELECT COALESCE(SUM(pd.number * pd.price), 0) FROM PurchasingDepartment pd WHERE pd.profilePD = :profilePD")
    Long calculateTotalFullAmountByProfilePD(@Param("profilePD") ProfilePD profilePD);

    @Query("SELECT msp FROM PurchasingDepartment msp JOIN msp.profilePD  w WHERE w.filial.id = :filialId")
    List<PurchasingDepartment> findByFilialId(@Param("filialId") Long filialId);

    List<PurchasingDepartment> findAllByProfilePDId(Long id);
}

