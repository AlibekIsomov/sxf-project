package com.sxf.project.service;


import com.sxf.project.dto.UserDTO;
import com.sxf.project.entity.User;
import com.sxf.project.payload.AccountUpdateDTO;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.vm.UserVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    UserDTO getById(Long id);

    Page<UserDTO> getAll(Pageable pageable);

    boolean changePassword(UserVM userVM);


    ApiResponse accountUpdate(User user , AccountUpdateDTO accountUpdateDTO);

    ApiResponse updateUser(Long id, UserDTO user);

    ApiResponse create(UserDTO user);

    UserDTO getCurrentUser();

    Optional<User> getByUsername(String login);

    Page<User> search(String key, Pageable pageable);

    ApiResponse delete(Long id);
}