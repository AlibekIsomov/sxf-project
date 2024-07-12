package com.sxf.project.service;

import com.sxf.project.dto.PurchasingDepartmentDTO;
import com.sxf.project.entity.ProfilePD;
import com.sxf.project.entity.PurchasingDepartment;
import com.sxf.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PurchasingDepartmentService {
    Page<PurchasingDepartment> getAll(Pageable pageable) throws Exception;

    List<PurchasingDepartment> getAllByProfileDBId(Long id);

    Optional<PurchasingDepartment> getById(Long id, User currentUser) throws Exception;

    Optional<PurchasingDepartment> create(PurchasingDepartmentDTO data, User currentUser) throws Exception;

    Optional<PurchasingDepartment> update(Long id, PurchasingDepartmentDTO data, User currentUser) throws Exception;

    Page<PurchasingDepartment> getAllByNameContains(String name, Pageable pageable);

    Long getTotalFullAmountByProfilePD(Long profilePDId);

    Long getRemainingPaymentByProfilePD(Long profilePDId);

    void deleteById(Long id, User currentUser);
}
