package com.sxf.project.service;

import com.sxf.project.dto.CostumerDepartmentDTO;
import com.sxf.project.entity.CostumerDepartment;
import com.sxf.project.entity.PurchasingDepartment;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CostumerDepartmentService {

    Page<CostumerDepartment> getAll(Pageable pageable) throws Exception;

    List<CostumerDepartment> getAllByProfileDBId(Long id);

    Optional<CostumerDepartment> getById(Long id, User currentUser) throws Exception;

    ApiResponse create(CostumerDepartmentDTO data, User currentUser) throws Exception;

    Page<CostumerDepartment> getAllByNameContains(String name, Pageable pageable);

    Long getTotalFullAmountByProfilePD(Long profileCDId);

    Long getRemainingPaymentByProfilePD(Long profilePDId);

    ApiResponse deleteById(Long id, User currentUser);
}
