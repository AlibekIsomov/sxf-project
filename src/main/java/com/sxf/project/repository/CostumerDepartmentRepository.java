package com.sxf.project.repository;

import com.sxf.project.entity.CostumerDepartment;
import com.sxf.project.entity.ProfileCD;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CostumerDepartmentRepository extends JpaRepository<CostumerDepartment, Long> {
    Page<CostumerDepartment> findAllByName(String name, Pageable pageable);

    @Query("SELECT COALESCE(SUM(pd.number * pd.price), 0) FROM CostumerDepartment pd WHERE pd.profileCD = :profileCD")
    Long calculateTotalFullAmountByProfileCD(@Param("profileCD") ProfileCD profileCD);

    @Query("SELECT msp FROM CostumerDepartment msp JOIN msp.profileCD  w WHERE w.filial.id = :filialId")
    List<CostumerDepartment> findByFilialId(@Param("filialId") Long filialId);

    @Query("SELECT COALESCE(SUM(pd.payment), 0) FROM CostumerDepartment pd WHERE pd.profileCD = :profileCD")
    Long calculateTotalPayment(@Param("profileCD") ProfileCD profileCD);

    List<CostumerDepartment> findAllByProfileCDId(Long id);
}
