package com.sxf.project.controller;



import com.sxf.project.dto.UserDTO;
import com.sxf.project.entity.User;
import com.sxf.project.repository.UserRepository;
import com.sxf.project.security.JwtTokenUtil;
import com.sxf.project.security.Token;
import com.sxf.project.security.UserProvider;
import com.sxf.project.security.UserSpecial;
import com.sxf.project.service.UserService;
import com.sxf.project.vm.UserVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/authenticate")
    public ResponseEntity<Token> login(@RequestBody UserSpecial userSpecial) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userSpecial.getUsername(), userSpecial.getPassword()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        UserDetails userDetails = userProvider.loadUserByUsername(userSpecial.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails, true);
        return ResponseEntity.ok(new Token(token));

    }
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody User user) throws Exception {
        if (user.getId() != null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(userService.create(user));
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO user = userService.getCurrentUser();
        if (user.getId() != null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(user);
    }


    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody UserVM vm) {


        if(userService.changePassword(vm)){

            return ResponseEntity.noContent().build();

        }
        return  ResponseEntity.badRequest().build();
    }
}
