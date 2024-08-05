package com.sxf.project.controller;


import com.sxf.project.dto.UserDTO;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.repository.UserRepository;
import com.sxf.project.security.JwtTokenUtil;
import com.sxf.project.security.Token;
import com.sxf.project.security.UserProvider;
import com.sxf.project.security.UserSpecial;
import com.sxf.project.service.UserService;
import com.sxf.project.vm.UserVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequestMapping("api/account")
public class AccountController {
    @Autowired
    UserProvider userProvider;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@RequestBody UserSpecial userSpecial) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userSpecial.getUsername(), userSpecial.getPassword()));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("Akkaount o'chirilgan!", false));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("Login yoki parol xato!", false));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Jiddiy muammo bo'ldi!", false));
        }

        UserDetails userDetails = userProvider.loadUserByUsername(userSpecial.getUsername());

        String token = jwtTokenUtil.generateToken(userDetails, true);

        return ResponseEntity.ok(new ApiResponse("Muvaffaqiyatli kirildi!", true, token));
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/register")
//    public ResponseEntity<UserDTO> register(@RequestBody User user) throws Exception {
//        if (user.getId() == null)
//            return ResponseEntity.badRequest().build();
//        return ResponseEntity.ok(userService.create(user));
//    }


    @GetMapping("/current-user")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO user = userService.getCurrentUser();
        if (user.getId() != null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody UserVM vm) {

        if (userService.changePassword(vm)) {

            return ResponseEntity.noContent().build();

        }
        return ResponseEntity.badRequest().build();
    }
}
