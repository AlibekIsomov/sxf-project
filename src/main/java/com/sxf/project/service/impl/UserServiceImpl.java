package com.sxf.project.service.impl;


import com.sxf.project.converter.AbstractDTOConverter;
import com.sxf.project.dto.UserDTO;
import com.sxf.project.entity.User;
import com.sxf.project.payload.AccountUpdateDTO;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.repository.UserRepository;
import com.sxf.project.service.UserService;
import com.sxf.project.vm.UserVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl  implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    private final AbstractDTOConverter<User, UserDTO> converter;

    public UserServiceImpl(AbstractDTOConverter<User, UserDTO> converter) {
        this.converter = converter;
    }

    @Override
    public UserDTO getById(Long id) {
        return converter.convert(userRepository.findById(id).orElse(null));
    }


    @Override
    public Page<UserDTO> getAll(Pageable pageable) {
        Page<User> entity = userRepository.findAll(pageable);

        if (entity.isEmpty()) {
            return Page.empty();
        }

        return converter.convertList(entity);
    }

    @Override
    public boolean changePassword(UserVM vm) {
        Optional<User> user = userRepository.findByUsername(vm.getUsername());
        if (user.isPresent() && encoder.matches(vm.getOldPassword(), user.get().getPassword())) {
            user.get().setPassword(encoder.encode(vm.getNewPassword()));
            userRepository.save(user.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ApiResponse accountUpdate(User user, AccountUpdateDTO accountUpdateDTO){

        boolean existsByUsernameAndIdNot = userRepository.existsByUsernameAndIdNot(accountUpdateDTO.getUsername(),  user.getId());
        if(existsByUsernameAndIdNot) return new ApiResponse("Bunday usernamelik user tizimda mavjud!",false);
        user.setName(accountUpdateDTO.getName());
        user.setSurname(accountUpdateDTO.getSurname());
        user.setUsername(accountUpdateDTO.getUsername());
        user.setPhoneNumber(accountUpdateDTO.getPhoneNumber());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(user.getPassword()));
        }

        User savedUser = userRepository.save(user);
        return new ApiResponse("User muvaffaqiyatli yangilandi" , true, savedUser);
    }

    @Override
    public ApiResponse updateUser(Long id, UserDTO user){
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return new ApiResponse("Bunaqa idlik user yo'q" , false);
        }

        boolean existsByUsernameAndIdNot = userRepository.existsByUsernameAndIdNot(user.getUsername(),  user.getId());
        if(existsByUsernameAndIdNot) return new ApiResponse("Bunday usernamelik user tizimda mavjud!",false);

        existingUser.setName(user.getName());
        existingUser.setSurname(user.getSurname());
        existingUser.setUsername(user.getUsername());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setRoles(user.getRoles());

        if (existingUser.getPassword() != null && !existingUser.getPassword().isEmpty()) {
            existingUser.setPassword(encoder.encode(existingUser.getPassword()));
        }

        User savedUser = userRepository.save(existingUser);
        UserDTO responseDTO = converter.convert(savedUser);
        return new ApiResponse("User muvaffaqiyatli yangilandi" , true, responseDTO);
    }

    @Override
    public ApiResponse create(UserDTO userDTO) {
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            return new ApiResponse("Parol qo'yich majburiy!", false);
        }

        boolean existsByUsername = userRepository.existsByUsername(userDTO.getUsername());
        if (existsByUsername) {
            return new ApiResponse("Bunaqa foydalanuvchi nomi oldindan bor!", false);
        }
        User newUser = new User();
        newUser.setName(userDTO.getName());
        newUser.setSurname(userDTO.getSurname());
        newUser.setUsername(userDTO.getUsername());
        newUser.setPhoneNumber(userDTO.getPhoneNumber());
        newUser.setRoles(userDTO.getRoles());
        newUser.setPassword(encoder.encode(userDTO.getPassword()));
        newUser.setActive(userDTO.getActive() != null ? userDTO.getActive() : false);

        User savedUser = userRepository.save(newUser);
        UserDTO responseDTO = converter.convert(savedUser);
        return new ApiResponse("User muvaffaqiyatli yaratildi", true, responseDTO);
    }



    @Override
    public UserDTO getCurrentUser() {
        String username = getPrincipal();
        if (username != null)
            return userRepository.findByUsername(username).map(UserDTO::new).orElse(null);
        return null;
    }

        private String getPrincipal() {
        String userName = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Page<User> search(String key, Pageable pageable) {

        try {
            Long n = Long.parseLong(key);
            return userRepository.findAllByIdOrNameContainsIgnoreCaseOrSurnameContainsIgnoreCaseOrUsernameContainsIgnoreCaseOrPhoneNumberContainsIgnoreCase(n, key, key, key, key, pageable);
        } catch (Exception x) {
            return userRepository.findAllByIdOrNameContainsIgnoreCaseOrSurnameContainsIgnoreCaseOrUsernameContainsIgnoreCaseOrPhoneNumberContainsIgnoreCase((long) -1, key, key, key, key, pageable);
        }
    }

    @Override
    public ApiResponse delete(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return new ApiResponse("Bunday Idlik user yo'q", false);
        }
        User user = optionalUser.get();
        userRepository.deleteById(id);
        return new ApiResponse("User muvafaqqiyatli o'chirildi", true, user);
    }

}