package com.sxf.project.controller;


import com.sxf.project.dto.WorkerDTO;
import com.sxf.project.entity.User;
import com.sxf.project.entity.Worker;
import com.sxf.project.repository.WorkerRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/worker")
public class WorkerController{

    @Autowired
    WorkerService workerService;
    @Autowired
    WorkerRepository workerRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Worker> getById(@PathVariable Long id, @CurrentUser User currentUser) throws Exception {
        return workerService.getById(id,currentUser).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<Page<Worker>> getAll(Pageable pageable) throws Exception {
        return ResponseEntity.ok(workerService.getAll(pageable));
    }

    @GetMapping("/byFilial")
    public ResponseEntity<Page<Worker>> getAllByCurrentUserFilial(@CurrentUser User currentUser, Pageable pageable) throws Exception {
        if (currentUser.getAssignedFilial() == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Long filialId = currentUser.getAssignedFilial().getId();
        Page<Worker> workers = workerService.getAllByFilial(filialId, pageable);
        return ResponseEntity.ok(workers);
    }

    @PostMapping
    public ResponseEntity<Worker> create(@RequestBody WorkerDTO data, @CurrentUser User currentUser) throws Exception {
        try {
            Optional<Worker> createdWorker = workerService.create(data, currentUser);

            if(createdWorker.isPresent()){
                return ResponseEntity.ok(createdWorker.get());
            }
            else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Worker> update(@PathVariable Long id,
                                         @RequestBody WorkerDTO data , @CurrentUser User currentUser) {
        try {
            Optional<Worker> updatedWorker = workerService.update(id, data, currentUser);

            if (updatedWorker.isPresent()) {
                return ResponseEntity.ok(updatedWorker.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ChangeSetPersister.NotFoundException categoryNotFoundException) {

            return ResponseEntity.badRequest().build();
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id, @CurrentUser User currentUser) {
        workerService.deleteById(id, currentUser);
    }

    @GetMapping("/search-name/{name}")
    public ResponseEntity<Page<Worker>> searchName(@PathVariable String name, String surname, Pageable pageable) {
        return ResponseEntity.ok(workerService.getAllByNameAndSurnameContains(name, surname, pageable));
    }
}
