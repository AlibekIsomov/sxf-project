package com.sxf.project.service;

import com.sxf.project.entity.FileEntity;

import java.util.List;

public interface FileService {

    List<FileEntity> getAll(String key);

    FileEntity getById(Long id);

    FileEntity create(FileEntity entity);

    FileEntity update(FileEntity entity);

    void delete(FileEntity entity);

    void deleteById(Long dataId);
}
