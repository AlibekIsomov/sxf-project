package com.sxf.project.controller;


import com.sxf.project.dto.PurchasingDepartmentDTO;
import com.sxf.project.entity.PurchasingDepartment;
import com.sxf.project.entity.User;
import com.sxf.project.repository.PurchasingDepartmentRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.PurchasingDepartmentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    public ResponseEntity<PurchasingDepartment> create(@RequestBody PurchasingDepartmentDTO data, @CurrentUser User currentUser) throws Exception {
        try {
            Optional<PurchasingDepartment> purchasingDepartmentCreate = purchasingDepartmentService.create(data, currentUser);

            if (purchasingDepartmentCreate.isPresent()) {
                return ResponseEntity.ok(purchasingDepartmentCreate.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchasingDepartment> update(@PathVariable Long id,
                                                       @RequestBody PurchasingDepartmentDTO data,
                                                       @CurrentUser User currentUser) throws Exception {
        try {
            Optional<PurchasingDepartment> updatedStore = purchasingDepartmentService.update(id, data, currentUser);

            if (updatedStore.isPresent()) {
                return ResponseEntity.ok(updatedStore.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (NoSuchElementException storeNotFoundException) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/full-amount/{profilePDId}")
    public ResponseEntity<Long> getTotalFullAmount(@PathVariable Long profilePDId) {
        Long totalFullAmount = purchasingDepartmentService.getTotalFullAmountByProfilePD(profilePDId);
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
        if (!purchasingDepartments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bunaqa ID lik filal yo'q");
        }
        return ResponseEntity.ok(purchasingDepartments);
    }

}
