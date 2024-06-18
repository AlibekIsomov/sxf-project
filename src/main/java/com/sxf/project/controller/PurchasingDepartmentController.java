package com.sxf.project.controller;


import com.sxf.project.dto.PurchasingDepartmentDTO;
import com.sxf.project.entity.PurchasingDepartment;
import com.sxf.project.repository.PurchasingDepartmentRepository;
import com.sxf.project.service.PurchasingDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
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
    public ResponseEntity<PurchasingDepartment> getById(@PathVariable Long id) throws Exception {
        return purchasingDepartmentService.getById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PurchasingDepartment> create(@RequestBody PurchasingDepartmentDTO data) throws Exception {
        try {
            Optional<PurchasingDepartment> purchasingDepartmentCreate = purchasingDepartmentService.create(data);

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
    public ResponseEntity<PurchasingDepartment> update(@PathVariable Long id, @RequestBody PurchasingDepartmentDTO data) {
        try {
            Optional<PurchasingDepartment> updatedStore = purchasingDepartmentService.update(id, data);

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

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        purchasingDepartmentService.deleteById(id);
    }

}
