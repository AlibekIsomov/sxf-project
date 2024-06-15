package com.sxf.project.service.impl;

import com.sxf.project.dto.ProfilePDDTO;
import com.sxf.project.entity.*;
import com.sxf.project.repository.FileRepository;
import com.sxf.project.repository.FilialRepository;
import com.sxf.project.repository.ProfilePDRepository;
import com.sxf.project.service.ProfilePDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfilePDServiceImpl implements ProfilePDService {

    private static final Logger logger = LoggerFactory.getLogger(ProfilePDServiceImpl.class);

    @Autowired
    ProfilePDRepository profilePDRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    FilialRepository filialRepository;


    @Override
    public Page<ProfilePD> getAll(Pageable pageable) throws Exception {
        return profilePDRepository.findAll(pageable);
    }

    @Override
    public Optional<ProfilePD> getById(Long id) throws Exception {
        if(!profilePDRepository.existsById(id)) {
            logger.info("ProfilePD with id " + id + " does not exists");
            return Optional.empty();
        }
        return profilePDRepository.findById(id);
    }

    @Override
    public Optional<ProfilePD> create(ProfilePDDTO data) throws Exception {

        Optional<Filial> optionalFilial = filialRepository.findById(data.getFilialId());

        if (!optionalFilial.isPresent()) {
            logger.info("Such ID filial does not exist!");
        }

        ProfilePD profilePD = new ProfilePD();
        profilePD.setName(data.getName());
        profilePD.setDescription(data.getDescription());

        profilePD.setFilial(optionalFilial.get());

        return Optional.of(profilePDRepository.save(profilePD));
    }

    @Override
    public Optional<ProfilePD> update(Long id, ProfilePDDTO data) throws Exception {
        Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(id);

        if (optionalProfilePD.isPresent()) {
            ProfilePD profilePDUpdate = optionalProfilePD.get();

            FileEntity oldFileEntity = profilePDUpdate.getFileEntity();
            // Check if fileId is provided before removing the FileEntity
            if (data.getFileEntityId() != null) {
                Optional<FileEntity> newFileEntityOptional = fileRepository.findById(data.getFileEntityId());

                if (!newFileEntityOptional.isPresent()) {
                    logger.info("FileEntity with id " + data.getFileEntityId() + " does not exist");
                    return Optional.empty();
                }

                // Set the new FileEntity
                FileEntity newFileEntity = newFileEntityOptional.get();
                profilePDUpdate.setFileEntity(newFileEntity);
            } else {
                // If fileId is not provided, remove the old FileEntity
                if (oldFileEntity != null) {
                    // Delete the old FileEntity from the repository
                    fileRepository.delete(oldFileEntity);
                    // Remove the old FileEntity from the Store
                    profilePDUpdate.setFileEntity(null);
                }
            }
            profilePDUpdate.setName(data.getName());
            profilePDUpdate.setDescription(data.getDescription());


            // profilePDUpdate.setFilial(optionalFilial.get());

            return Optional.of(profilePDRepository.save(profilePDUpdate));
        } else {
            throw new ChangeSetPersister.NotFoundException();
        }
    }
    @Override
    public Page<ProfilePD> getAllByNameContains(String name, Pageable pageable) {
        return profilePDRepository.findAllByName(name, pageable);
    }

    @Override
    public void deleteById(Long id) {
        if(!profilePDRepository.existsById(id)) {
            logger.info("ProfilePD with id " + id + " does not exists");
        };
        profilePDRepository.deleteById(id);
    }
}