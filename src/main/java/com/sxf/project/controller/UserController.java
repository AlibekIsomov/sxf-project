package com.sxf.project.controller;


import com.sxf.project.dto.UserDTO;
import com.sxf.project.entity.User;
import com.sxf.project.payload.AccountUpdateDTO;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.repository.UserRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RestController
@RequestMapping("api/user")
public class UserController  {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;


//    @PreAuthorize("hasRole('ADMIN')")
//    @RequestMapping("/search/{key}")
//    public ResponseEntity<?> search(@PathVariable String key, Pageable pageable){
//        return ResponseEntity.ok(userService.search(key,pageable));
//    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/all")
//    public ResponseEntity<?> getAll(Pageable pageable) {
//        return ResponseEntity.ok(userService.getAll(pageable));
//    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/get/{id}")
//    public ResponseEntity<?> getById(@PathVariable Long id){
//        return ResponseEntity.ok(userService.getById(id));
//    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO user) {
        ApiResponse apiResponse = userService.updateUser(id, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/accountEdit")
    public ResponseEntity<?> accountUpdate(@CurrentUser User user, @RequestBody AccountUpdateDTO accountUpdateDTO) {
        ApiResponse apiResponse = userService.accountUpdate(user, accountUpdateDTO);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserDTO user) {
        ApiResponse apiResponse = userService.create(user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll(Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ApiResponse apiResponse = userService.delete(id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }


}
