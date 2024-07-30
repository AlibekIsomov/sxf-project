package com.sxf.project.controller;


import com.sxf.project.dto.CostumerDepartmentDTO;
import com.sxf.project.entity.CostumerDepartment;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.repository.CostumerDepartmentRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.CostumerDepartmentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/costumerDepartment")
public class CostumerDepartmentController {
    @Autowired
    CostumerDepartmentService costumerDepartmentService;

    @Autowired
    CostumerDepartmentRepository costumerDepartmentRepository;

    @Transactional
    @GetMapping
    public ResponseEntity<Page<CostumerDepartment>> getAll(Pageable pageable) throws Exception {
        return ResponseEntity.ok(costumerDepartmentService.getAll(pageable));
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<CostumerDepartment> getById(@PathVariable Long id, @CurrentUser User currentUser) throws Exception {
        return costumerDepartmentService.getById(id, currentUser).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CostumerDepartmentDTO data, @CurrentUser User currentUser) throws Exception {
        ApiResponse apiResponse = costumerDepartmentService.create(data, currentUser);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody CostumerDepartmentDTO data,
                                    @CurrentUser User currentUser) throws Exception {
        ApiResponse apiResponse = costumerDepartmentService.update(id, data, currentUser);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }


    @GetMapping("/full-amount/{profilePDId}")
    public ResponseEntity<Long> getTotalFullAmount(@PathVariable Long profilePDId) {
        Long totalFullAmount = costumerDepartmentService.getTotalFullAmountByProfilePD(profilePDId);
        return ResponseEntity.ok(totalFullAmount);
    }

    @GetMapping("/remaining-payment/{profilePDId}")
    public ResponseEntity<Long> getRemainingPayment(@PathVariable Long profilePDId) {
        Long remainingPayment = costumerDepartmentService.getRemainingPaymentByProfilePD(profilePDId);
        return ResponseEntity.ok(remainingPayment);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id,
                           @CurrentUser User currentUser) throws Exception {
        costumerDepartmentService.deleteById(id, currentUser);
    }

    @GetMapping("/profilePD/{id}")
    public ResponseEntity<?> getProfilePDByFilial(@PathVariable Long id) {
        List<CostumerDepartment> purchasingDepartments = costumerDepartmentService.getAllByProfileCDId(id);
        if (purchasingDepartments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bunaqa ID lik filal yo'q");
        }
        return ResponseEntity.ok(purchasingDepartments);
    }
}
