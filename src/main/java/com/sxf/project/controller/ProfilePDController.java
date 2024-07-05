package com.sxf.project.controller;


import com.sxf.project.dto.ProfilePDDTO;
import com.sxf.project.entity.ProfilePD;
import com.sxf.project.entity.User;
import com.sxf.project.repository.ProfilePDRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.ProfilePDService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/profilPD")
public class ProfilePDController {

    @Autowired
    ProfilePDService profilePDService;
    @Autowired
    ProfilePDRepository profilePDRepository;

    @Transactional
    @GetMapping
    public ResponseEntity<Page<ProfilePD>> getAll(Pageable pageable) throws Exception {
        return ResponseEntity.ok(profilePDService.getAll(pageable));
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<ProfilePD> getById(@PathVariable Long id, @CurrentUser User currentUser) throws Exception {
        return profilePDService.getById(id, currentUser).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProfilePD> create(@RequestBody ProfilePDDTO data, @CurrentUser User currentUser) throws Exception {
        try {
            Optional<ProfilePD> createProfilePD = profilePDService.create(data, currentUser);

            if (createProfilePD.isPresent()) {
                return ResponseEntity.ok(createProfilePD.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfilePD> update(@PathVariable Long id, @RequestBody ProfilePDDTO data, @CurrentUser User currentUser) {
        try {
            Optional<ProfilePD> updatedProfilePD = profilePDService.update(id, data, currentUser);

            if (updatedProfilePD.isPresent()) {
                return ResponseEntity.ok(updatedProfilePD.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (NoSuchElementException storeNotFoundException) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id, @CurrentUser User currentUser) {
        profilePDService.deleteById(id, currentUser);
    }
}
