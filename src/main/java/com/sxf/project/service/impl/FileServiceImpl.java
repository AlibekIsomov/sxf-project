package com.sxf.project.service.impl;




import com.sxf.project.entity.FileEntity;
import com.sxf.project.repository.FileRepository;
import com.sxf.project.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    FileRepository fileRepository;

    @Override
    public List<FileEntity> getAll(String key) {
        return fileRepository.findAll();
    }

    @Override
    public FileEntity getById(Long id) {
        return fileRepository.findById(id).orElseThrow(()->new RuntimeException("not found"));
    }

    @Override
    public FileEntity create(FileEntity entity) {
        if(entity.getId() == null)
        return fileRepository.save(entity);
        throw new RuntimeException("id must be null");
    }

    @Override
    public FileEntity update(FileEntity entity) {
        if(entity.getId() != null)
            return fileRepository.save(entity);
        throw new RuntimeException("id must not be null");
    }

    @Override
    public void delete(FileEntity entity) {
        fileRepository.delete(entity);
    }

    @Override
    public void deleteById(Long dataId) {
        fileRepository.deleteById(dataId);
    }
}
