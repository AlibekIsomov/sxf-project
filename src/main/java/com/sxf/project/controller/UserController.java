package com.sxf.project.controller;


import com.sxf.project.dto.UserDTO;
import com.sxf.project.entity.User;
import com.sxf.project.repository.UserRepository;
import com.sxf.project.service.CommonServiceDto;
import com.sxf.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequestMapping("api/user")
public class UserController extends AbstractDTOController<User, UserDTO> {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    public UserController(CommonServiceDto<User, UserDTO> service) {
        super(service);
    }

    @RequestMapping("/search/{key}")
    public ResponseEntity<?> search(@PathVariable String key, Pageable pageable){
        return ResponseEntity.ok(userService.search(key,pageable));
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAll(Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUserDetails) {
        // Fetch the existing user from repository
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        // Update fields other than password
        existingUser.setName(updatedUserDetails.getName());
        existingUser.setSurname(updatedUserDetails.getSurname());
        existingUser.setUsername(updatedUserDetails.getUsername());
        existingUser.setPhoneNumber(updatedUserDetails.getPhoneNumber());
        existingUser.setRoles(updatedUserDetails.getRoles());

        // Save the updated user
        User savedUser = userRepository.save(existingUser);
        return ResponseEntity.ok(savedUser);
    }
   

}
