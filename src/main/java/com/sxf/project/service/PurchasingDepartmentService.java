package com.sxf.project.service;

import com.sxf.project.dto.PurchasingDepartmentDTO;
import com.sxf.project.entity.PurchasingDepartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PurchasingDepartmentService {
    Page<PurchasingDepartment> getAll(Pageable pageable) throws Exception;

    Optional<PurchasingDepartment> getById(Long id) throws Exception;

    Optional<PurchasingDepartment> create(PurchasingDepartmentDTO data) throws Exception;

    Optional<PurchasingDepartment> update(Long id, PurchasingDepartmentDTO data) throws Exception;

    Page<PurchasingDepartment> getAllByNameContains(String name, Pageable pageable);

    Long getTotalFullAmountByProfilePD(Long profilePDId);

    Long getRemainingPaymentByProfilePD(Long profilePDId);

    void deleteById(Long id);
}
