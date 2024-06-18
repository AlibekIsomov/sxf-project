package com.sxf.project.repository;


import com.sxf.project.entity.Role;
import com.sxf.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends DistributedRepository<User> {

    public Page<User> findAllByOrderByIdDesc(Pageable pageable);

    public Page<User> findAllByIdOrNameContainsIgnoreCaseOrSurnameContainsIgnoreCaseOrUsernameContainsIgnoreCaseOrEmailContainsIgnoreCase(Long id, String name, String surname, String username, String email, Pageable pageable);

    Optional<User> findByUsername(String username);

    List<User> findAllByRolesContains(Role role);
}
