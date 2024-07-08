package com.sxf.project.service;

import com.sxf.project.dto.FilialDTO;
import com.sxf.project.entity.Filial;
import com.sxf.project.entity.User;
import com.sxf.project.service.impl.FilialServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

public interface FilialService {
    Page<Filial> getAll(Pageable pageable) throws Exception;

    Optional<Filial> getById(Long id) throws Exception;

    List<Filial> getAllManagers(User user);

    Optional<Filial> create(FilialDTO data) throws Exception;

    Optional<Filial> update(Long id, FilialDTO data) throws Exception;

    Page<Filial> getAllByNameContains(String name, Pageable pageable);

    ByteArrayInputStream exportExcel() throws IOException;

    void deleteById(Long id);

    Filial getFilialForUser(Long filialId, Long userId) throws AccessDeniedException;

    List<Filial> getAllFilialsForAdmin(Long adminId) throws AccessDeniedException;


    FilialDTO assignFilialToManager(Long filialId, Long managerId) throws AccessDeniedException;

    @Transactional
    FilialDTO unassignFilialFromManager(Long managerId) throws FilialServiceImpl.AccessDeniedException;
}
