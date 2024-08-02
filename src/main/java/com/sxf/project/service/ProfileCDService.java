package com.sxf.project.service;

import com.sxf.project.dto.ProfileCDDTO;
import com.sxf.project.entity.ProfileCD;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProfileCDService {
    Page<ProfileCD> getAll(Pageable pageable) throws Exception;

    List<ProfileCD> getAllByFilial(Long id);

    List<ProfileCD> getProfileCDByFilial(User user);

    Optional<ProfileCD> getById(Long id, User currentUser) throws Exception;


    ApiResponse create(ProfileCDDTO data, User currentUser) throws Exception;

    ApiResponse update(Long id, ProfileCDDTO data, User currentUser) throws Exception;

    void deleteById(Long id, User currentUser);

    ByteArrayInputStream getByFilialIdExportExcel(Long filialId) throws IOException;
}
