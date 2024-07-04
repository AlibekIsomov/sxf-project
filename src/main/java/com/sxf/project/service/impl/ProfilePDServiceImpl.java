package com.sxf.project.service.impl;

import com.sxf.project.dto.ProfilePDDTO;
import com.sxf.project.entity.*;
import com.sxf.project.repository.FileRepository;
import com.sxf.project.repository.FilialRepository;
import com.sxf.project.repository.ProfilePDRepository;
import com.sxf.project.service.ProfilePDService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    public Optional<ProfilePD> getById(Long id, User currentUser) throws Exception {

        Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(id);

        if (optionalProfilePD.isPresent()) {
            ProfilePD checkProfileDp = optionalProfilePD.get();
            if (!checkProfileDp.getFilial().getId().equals(currentUser.getAssignedFilial().getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
                throw new AccessDeniedException("Restricted for this manager");
            }
        }
        return profilePDRepository.findById(id);
    }

    @Override
    public Optional<ProfilePD> create(ProfilePDDTO data, User currentUser) throws Exception {


        Optional<Filial> optionalFilial = filialRepository.findById(data.getFilialId());

        if (!optionalFilial.isPresent()) {
            logger.info("Such ID filial does not exist!");

            Filial checkFilial = optionalFilial.get();
            if (!checkFilial.getId().equals(currentUser.getAssignedFilial().getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
                throw new AccessDeniedException("Restricted for this manager");
            }
            return Optional.empty();

        }

        ProfilePD profilePD = new ProfilePD();
        profilePD.setName(data.getName());
        profilePD.setDescription(data.getDescription());

        profilePD.setFilial(optionalFilial.get());

        return Optional.of(profilePDRepository.save(profilePD));
    }

    @Override
    public Optional<ProfilePD> update(Long id, ProfilePDDTO data, User currentUser) throws Exception {
        Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(id);

        ProfilePD checkProfileDp = optionalProfilePD.get();


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

                if(!checkProfileDp.getFilial().getId().equals(currentUser.getAssignedFilial().getId())) {
                    logger.info("Restricted for this manager");
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
    public void deleteById(Long id, User currentUser) {
        Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(id);

        if (optionalProfilePD.isPresent()) {
            ProfilePD checkProfileDp = optionalProfilePD.get();
            if (!checkProfileDp.getFilial().getId().equals(currentUser.getAssignedFilial().getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
                throw new AccessDeniedException("Restricted for this manager");
            }
        };
        profilePDRepository.deleteById(id);
    }

    @Override
    public ByteArrayInputStream getByFilialIdExportExcel(Long filialId) throws IOException {
        List<ProfilePD> purchasingDepartmentProfiles = profilePDRepository.findByFilialId(filialId);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Profiles");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Description");
        headerRow.createCell(3).setCellValue("Filial");
        headerRow.createCell(4).setCellValue("Created At");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int rowIdx = 1;
        for (ProfilePD purchasingDepartmentProfile : purchasingDepartmentProfiles) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(purchasingDepartmentProfile.getId());
            row.createCell(1).setCellValue(purchasingDepartmentProfile.getName());
            row.createCell(2).setCellValue(purchasingDepartmentProfile.getDescription());
            row.createCell(3).setCellValue(purchasingDepartmentProfile.getFilial().getName());

            LocalDateTime createdAtLocalDateTime = LocalDateTime.ofInstant(purchasingDepartmentProfile.getCreatedAt(), ZoneId.systemDefault());
            String formattedCreatedAt = createdAtLocalDateTime.format(dateFormatter);
            row.createCell(4).setCellValue(formattedCreatedAt);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}