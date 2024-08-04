package com.sxf.project.controller;


import com.sxf.project.dto.FilialDTO;
import com.sxf.project.entity.Filial;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.payload.ResourceNotFoundException;
import com.sxf.project.repository.FilialRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.FilialService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/filial")
public class FilialController {

    @Autowired
    FilialService filialService;
    @Autowired
    FilialRepository filialRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @GetMapping
    public ResponseEntity<Page<Filial>> getAll(Pageable pageable) throws Exception {
        return ResponseEntity.ok(filialService.getAll(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<Filial> getById(@PathVariable Long id) throws Exception {
        return filialService.getById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody FilialDTO data) throws Exception {
           ApiResponse apiResponse = filialService.create(data);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/managers")
    public ResponseEntity<List<Filial>> getAllManagers(@CurrentUser User currentUser) {
        List<Filial> filialList = filialService.getAllManagers(currentUser);
        if (filialList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.ok(filialList);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody FilialDTO data) throws Exception {
        ApiResponse apiResponse = filialService.update(id, data);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportExcel() throws IOException {
        ByteArrayInputStream in = filialService.exportExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=profiles.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        filialService.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{filialId}/assign-manager/{managerId}")
    public ResponseEntity<?> assignFilialToManager(@PathVariable Long filialId, @PathVariable Long managerId) {
        ApiResponse apiResponse = filialService.assignFilialToManager(filialId ,managerId);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{managerId}/unassign")
    public ResponseEntity<?> unassignFilialFromManager(@PathVariable Long managerId) {
        ApiResponse apiResponse = filialService.unassignFilialFromManager(managerId);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("getUser/{filialId}")
    public ResponseEntity<Filial> getFilialForUser(@PathVariable Long filialId, @RequestParam Long userId) throws AccessDeniedException {
        Filial filial = filialService.getFilialForUser(filialId, userId);
        return ResponseEntity.ok(filial);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<Filial>> getAllFilials(@RequestParam Long adminId) throws AccessDeniedException {
        List<Filial> filials = filialService.getAllFilialsForAdmin(adminId);
        return ResponseEntity.ok(filials);
    }
}
