package com.sxf.project.service;

import com.sxf.project.dto.BaseDTO;
import com.sxf.project.entity.DistributedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommonServiceDto<ENTITY extends DistributedEntity, DTO extends BaseDTO> {
    Page<DTO> getAll(Pageable pageable);

    DTO create(ENTITY entity) throws Exception;

    DTO update(ENTITY entity);

    DTO getById(Long id);

    boolean delete(Long id);
}
