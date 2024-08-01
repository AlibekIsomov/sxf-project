package com.sxf.project.controller;


import com.sxf.project.dto.PurchasingDepartmentDTO;
import com.sxf.project.entity.PurchasingDepartment;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.repository.PurchasingDepartmentRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.PurchasingDepartmentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchasingDepartment")
public class PurchasingDepartmentController {

    @Autowired
    PurchasingDepartmentService purchasingDepartmentService;

    @Autowired
    PurchasingDepartmentRepository purchasingDepartmentRepository;

    @Transactional
    @GetMapping
    public ResponseEntity<Page<PurchasingDepartment>> getAll(Pageable pageable) throws Exception {
        return ResponseEntity.ok(purchasingDepartmentService.getAll(pageable));
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<PurchasingDepartment> getById(@PathVariable Long id, @CurrentUser User currentUser) throws Exception {
        return purchasingDepartmentService.getById(id, currentUser).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @Transactional
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PurchasingDepartmentDTO data, @CurrentUser User currentUser) {
            ApiResponse apiResponse = purchasingDepartmentService.create(data, currentUser);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                                       @RequestBody PurchasingDepartmentDTO data,
                                                       @CurrentUser User currentUser) {
            ApiResponse apiResponse = purchasingDepartmentService.update(id, data, currentUser);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }


    @GetMapping("/full-amount/{profilePDId}")
    public ResponseEntity<Long> getTotalPayment(@PathVariable Long profilePDId) {
        Long totalFullAmount = purchasingDepartmentService.getTotalPaymentByProfilePD(profilePDId);
        return ResponseEntity.ok(totalFullAmount);
    }

    @GetMapping("/remaining-payment/{profilePDId}")
    public ResponseEntity<Long> getRemainingPayment(@PathVariable Long profilePDId) {
        Long remainingPayment = purchasingDepartmentService.getRemainingPaymentByProfilePD(profilePDId);
        return ResponseEntity.ok(remainingPayment);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id,
                           @CurrentUser User currentUser) throws Exception {
        purchasingDepartmentService.deleteById(id, currentUser);
    }

    @GetMapping("/profilePD/{id}")
    public ResponseEntity<?> getProfilePDByFilial(@PathVariable Long id) {
        List<PurchasingDepartment> purchasingDepartments = purchasingDepartmentService.getAllByProfileDBId(id);
        return ResponseEntity.ok(purchasingDepartments);
    }

}
