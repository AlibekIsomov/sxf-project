package com.sxf.project.service;

import com.sxf.project.dto.FilialDTO;
import com.sxf.project.entity.Filial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FilialService {
    Page<Filial> getAll(Pageable pageable) throws Exception;

    Optional<Filial> getById(Long id) throws Exception;

    Optional<Filial> create(FilialDTO data) throws Exception;

    Optional<Filial> update(Long id, FilialDTO data) throws Exception;

    Page<Filial> getAllByNameContains(String name, Pageable pageable);

    void deleteById(Long id);
}
