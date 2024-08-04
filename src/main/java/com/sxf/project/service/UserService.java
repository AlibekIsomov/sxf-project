package com.sxf.project.service;


import com.sxf.project.dto.UserDTO;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.vm.UserVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService extends CommonServiceDto<User, UserDTO> {
//    public Optional<User> getByIdEntity(Long id);

//    public List<UserDTO> getAll();

//    UserDTO update(User user);

//    public Optional<UserDTO> getById(Long id);

//    UserDTO create(User user);

//    public void deleteById(Long id);

    boolean changePassword(UserVM userVM);

    ApiResponse updateUser(Long id, User user);

    UserDTO getCurrentUser();

    Optional<User> getByUsername(String login);

    Page<User> search(String key, Pageable pageable);
}