package com.sxf.project.repository;

import com.sxf.project.entity.PurchasingDepartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasingDepartmentRepository extends JpaRepository<PurchasingDepartment, Long> {
    Page<PurchasingDepartment> findAllByName(String name, Pageable pageable);
}
