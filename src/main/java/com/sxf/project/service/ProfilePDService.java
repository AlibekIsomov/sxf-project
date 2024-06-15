package com.sxf.project.service;

import com.sxf.project.dto.ProfilePDDTO;
import com.sxf.project.entity.ProfilePD;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProfilePDService {
    Page<ProfilePD> getAll(Pageable pageable) throws Exception;

    Optional<ProfilePD> getById(Long id) throws Exception;

    Optional<ProfilePD> create(ProfilePDDTO data) throws Exception;

    Optional<ProfilePD> update(Long id, ProfilePDDTO data) throws Exception;

    Page<ProfilePD> getAllByNameContains(String name, Pageable pageable);

    void deleteById(Long id);
}
