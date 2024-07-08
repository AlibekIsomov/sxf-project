package com.sxf.project.service;

import com.sxf.project.dto.ProfilePDDTO;
import com.sxf.project.entity.ProfilePD;
import com.sxf.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProfilePDService {
    Page<ProfilePD> getAll(Pageable pageable) throws Exception;

    List<ProfilePD> getAllByFilial(Long id);

    List<ProfilePD> getProfilePDByFilial(User user);

    Optional<ProfilePD> getById(Long id, User currentUser) throws Exception;

    Optional<ProfilePD> create(ProfilePDDTO data, User currentUser) throws Exception;

    Optional<ProfilePD> update(Long id, ProfilePDDTO data, User currentUser) throws Exception;

    Page<ProfilePD> getAllByNameContains(String name, Pageable pageable);

    void deleteById(Long id, User currentUser);

    ByteArrayInputStream getByFilialIdExportExcel(Long FilialId) throws IOException;
}
