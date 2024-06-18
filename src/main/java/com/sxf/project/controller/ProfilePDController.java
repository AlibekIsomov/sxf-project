package com.sxf.project.controller;


import com.sxf.project.dto.ProfilePDDTO;
import com.sxf.project.entity.ProfilePD;
import com.sxf.project.repository.ProfilePDRepository;
import com.sxf.project.service.ProfilePDService;
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
    public ResponseEntity<ProfilePD> getById(@PathVariable Long id) throws Exception {
        return profilePDService.getById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProfilePD> create(@RequestBody ProfilePDDTO data) throws Exception {
        try {
            Optional<ProfilePD> createProfilePD = profilePDService.create(data);

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
    public ResponseEntity<ProfilePD> update(@PathVariable Long id, @RequestBody ProfilePDDTO data) {
        try {
            Optional<ProfilePD> updatedProfilePD = profilePDService.update(id, data);

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

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        profilePDService.deleteById(id);
    }
}
