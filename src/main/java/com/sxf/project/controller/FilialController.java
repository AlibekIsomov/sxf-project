package com.sxf.project.controller;


import com.sxf.project.dto.FilialDTO;
import com.sxf.project.entity.Filial;
import com.sxf.project.repository.FilialRepository;
import com.sxf.project.service.FilialService;
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

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/filial")
public class FilialController {

    @Autowired
    FilialService filialService;
    @Autowired
    FilialRepository filialRepository;

    @Transactional
    @GetMapping
    public ResponseEntity<Page<Filial>> getAll(Pageable pageable) throws Exception {
        return ResponseEntity.ok(filialService.getAll(pageable));
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<Filial> getById(@PathVariable Long id) throws Exception {
        return filialService.getById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Filial> create(@RequestBody FilialDTO data) throws Exception {
        try {
            Optional<Filial> createFilial = filialService.create(data);

            if (createFilial.isPresent()) {
                return ResponseEntity.ok(createFilial.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Filial> update(@PathVariable Long id, @RequestBody FilialDTO data) {
        try {
            Optional<Filial> updatedFilal = filialService.update(id, data);

            if (updatedFilal.isPresent()) {
                return ResponseEntity.ok(updatedFilal.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (NoSuchElementException storeNotFoundException) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        filialService.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign")
    public ResponseEntity<Filial> assignFilialToManager(@RequestParam Long filialId, @RequestParam Long managerId) throws AccessDeniedException {
        Filial assignedFilial = filialService.assignFilialToManager(filialId, managerId);
        return ResponseEntity.ok(assignedFilial);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/{filialId}")
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
