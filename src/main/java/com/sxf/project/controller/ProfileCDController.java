package com.sxf.project.controller;


import com.sxf.project.dto.ProfileCDDTO;
import com.sxf.project.entity.ProfileCD;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.repository.ProfileCDRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.ProfileCDService;
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
import java.util.List;

@RestController
@RequestMapping("/api/profileCD")
public class ProfileCDController {
    @Autowired
    ProfileCDService profilePDService;
    @Autowired
    ProfileCDRepository profilePDRepository;

    @Transactional
    @GetMapping
    public ResponseEntity<Page<ProfileCD>> getAll(Pageable pageable) throws Exception {
        return ResponseEntity.ok(profilePDService.getAll(pageable));
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<ProfileCD> getById(@PathVariable Long id, @CurrentUser User currentUser) throws Exception {
        return profilePDService.getById(id, currentUser).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @GetMapping("/filial/{id}")
    public ResponseEntity<?> getProfilePDByFilial(@PathVariable Long id) {
        List<ProfileCD> profilePDS = profilePDService.getAllByFilial(id);
        if (profilePDS.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bunaqa ID lik filal yo'q");
        }
        return ResponseEntity.ok(profilePDS);
    }
    @Transactional
    @GetMapping("/filial")
    public ResponseEntity<?> getProfileCDByFilial(@CurrentUser User currentUser) {
        List<ProfileCD> profilePDS = profilePDService.getProfilePDByFilial(currentUser);
        return ResponseEntity.ok(profilePDS);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProfileCDDTO data, @CurrentUser User currentUser) throws Exception {
        ApiResponse apiResponse = profilePDService.create(data, currentUser);

        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProfileCDDTO data, @CurrentUser User currentUser)  throws Exception {
        ApiResponse apiResponse = profilePDService.update(id, data, currentUser);

        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }
    @Transactional
    @GetMapping("/export/{filialId}")
    public ResponseEntity<InputStreamResource> exportExcel(@PathVariable Long filialId) throws IOException {
        ByteArrayInputStream in = profilePDService.getByFilialIdExportExcel(filialId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=purchasingDepartmentProfile.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }
    @Transactional
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id, @CurrentUser User currentUser) {
        profilePDService.deleteById(id, currentUser);
    }
}
