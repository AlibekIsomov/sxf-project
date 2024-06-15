package com.sxf.project.service.impl;

import com.sxf.project.dto.FilialDTO;
import com.sxf.project.entity.FileEntity;
import com.sxf.project.entity.Filial;
import com.sxf.project.repository.FileRepository;
import com.sxf.project.repository.FilialRepository;
import com.sxf.project.service.FilialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FilialServiceImpl implements FilialService {
    private static final Logger logger = LoggerFactory.getLogger(FilialServiceImpl.class);

    @Autowired
    FilialRepository filialRepository;

    @Autowired
    FileRepository fileRepository;

    @Override
    public Page<Filial> getAll(Pageable pageable) throws Exception {
        return filialRepository.findAll(pageable);
    }

    @Override
    public Optional<Filial> getById(Long id) throws Exception {
        if (!filialRepository.existsById(id)) {
            logger.info("Input with id " + id + " does not exists");
            return Optional.empty();
        }
        return filialRepository.findById(id);
    }

    @Override
    public Optional<Filial> create(FilialDTO data) throws Exception {

        Optional<FileEntity> optionalFileEntity = fileRepository.findById(data.getFileEntityId());
        if (!optionalFileEntity.isPresent()) {
            logger.info("Such ID category does not exist!");
        }

        Filial filial = new Filial();
        filial.setName(data.getName());
        filial.setDescription(data.getDescription());
        filial.setFileEntity(optionalFileEntity.get());

        return Optional.of(filialRepository.save(filial));
    }

    @Override
    public Optional<Filial> update(Long id, FilialDTO data) throws Exception {
        Optional<Filial> existingFilial = filialRepository.findById(id);

        Filial filialUpdate = existingFilial.get();

        FileEntity oldFileEntity = filialUpdate.getFileEntity();

        if (data.getFileEntityId() != null) {
            Optional<FileEntity> newFileEntityOptional = fileRepository.findById(data.getFileEntityId());

            if (!newFileEntityOptional.isPresent()) {
                logger.info("FileEntity with id " + data.getFileEntityId() + " does not exist");
                return Optional.empty();
            }

            FileEntity newFileEntity = newFileEntityOptional.get();
            filialUpdate.setFileEntity(newFileEntity);
        } else {
            if (oldFileEntity != null) {
                // Delete the old FileEntity from the repository
                fileRepository.delete(oldFileEntity);
                // Remove the old FileEntity from the Store
                filialUpdate.setFileEntity(null);
            }
        }

        filialUpdate.setName(data.getName());
        filialUpdate.setDescription(data.getDescription());

        return Optional.of(filialRepository.save(filialUpdate));
    }

    @Override
    public Page<Filial> getAllByNameContains(String name, Pageable pageable) {
        return filialRepository.findAllByNameContains(name, pageable);
    }

    @Override
    public void deleteById(Long id) {
        if (!filialRepository.existsById(id)) {
            logger.info("Filial with id " + id + " does not exists");
        }
        filialRepository.deleteById(id);
    }

}
