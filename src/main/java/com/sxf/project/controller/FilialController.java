package com.sxf.project.controller;


import com.sxf.project.dto.FilialDTO;
import com.sxf.project.entity.Filial;
import com.sxf.project.repository.FilialRepository;
import com.sxf.project.service.FilialService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        filialService.deleteById(id);
    }
}
