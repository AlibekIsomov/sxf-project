package com.sxf.project.service.impl;

import com.sxf.project.dto.ProfilePDDTO;
import com.sxf.project.entity.*;
import com.sxf.project.payload.ApiResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
    public List<ProfilePD> getAllByFilial(Long id) {
        return profilePDRepository.findAllByFilialId(id);
    }

    @Override
    public List<ProfilePD> getProfilePDByFilial(User user) {
        if (user.getAssignedFilial() != null && user.getAssignedFilial().getId() != null) {
            return profilePDRepository.findAllByFilialId(user.getAssignedFilial().getId());
        } else {
            logger.info("User is not assigned to any filial");
            ResponseEntity.badRequest().body("Siz uchun hech qanday filial bog'lanmagan!");
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<ProfilePD> getById(Long id, User currentUser) throws Exception {
        Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(id);

        if (!optionalProfilePD.isPresent()) {
            logger.info("Such ID Profile does not exist!");
            return Optional.empty();
        }

        ProfilePD profilePD = optionalProfilePD.get();

        Filial workerFilial = profilePD.getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        if (currentUserFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            return Optional.empty();
        }

        if (currentUserFilial != null && !currentUserFilial.getId().equals(workerFilial.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
            return Optional.empty();
        }
        return profilePDRepository.findById(id);
    }

    @Override
    public ApiResponse create(ProfilePDDTO data, User currentUser) throws Exception {
        Optional<Filial> optionalFilial = filialRepository.findById(data.getFilialId());

        if (optionalFilial.isEmpty()) {
            logger.info("Filial with ID {} does not exist!", data.getFilialId());
            return new ApiResponse("Bunaqa Idlik filial yo'q!", false);
        }

        Filial checkFilial = optionalFilial.get();
        Filial assignedFilial = currentUser.getAssignedFilial();

        if (isUserRestricted(currentUser, assignedFilial, checkFilial)) {
            return new ApiResponse("Siz uchun hech qanday filial ulanmagan!", false);
        }

        ProfilePD profilePD = new ProfilePD();
        profilePD.setName(data.getName());
        profilePD.setDescription(data.getDescription());
        profilePD.setFilial(checkFilial);

        ProfilePD savedProfilePD = profilePDRepository.save(profilePD);
        return new ApiResponse("ProfilePD muvaffaqiyatli yaratildi!", true, savedProfilePD);
    }

    @Override
    public ApiResponse update(Long id, ProfilePDDTO data, User currentUser) throws Exception {
        Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(id);

        if (optionalProfilePD.isEmpty()) {
            logger.info("ProfilePD with ID {} does not exist!", id);
            return new ApiResponse("Bunaqa Idlik ProfilePD yo'q!", false);
        }

        ProfilePD profilePD = optionalProfilePD.get();
        Optional<Filial> optionalFilial = filialRepository.findById(data.getFilialId());

        if (optionalFilial.isEmpty()) {
            logger.info("Filial with ID {} does not exist!", data.getFilialId());
            return new ApiResponse("Bunaqa Idlik filial yo'q!", false);
        }

        Filial checkFilial = optionalFilial.get();
        Filial assignedFilial = currentUser.getAssignedFilial();

        if (isUserRestricted(currentUser, assignedFilial, checkFilial)) {
            return new ApiResponse("Siz uchun hech qanday filial ulanmagan!", false);
        }

        updateFileEntity(data, profilePD);

        profilePD.setName(data.getName());
        profilePD.setDescription(data.getDescription());
        profilePD.setFilial(checkFilial);

        ProfilePD updatedProfilePD = profilePDRepository.save(profilePD);
        return new ApiResponse("ProfilePD muvaffaqiyatli yangilandi!", true, updatedProfilePD);
    }

    private void updateFileEntity(ProfilePDDTO data, ProfilePD profilePD) {
        FileEntity oldFileEntity = profilePD.getFileEntity();

        if (data.getFileEntityId() != null) {
            Optional<FileEntity> newFileEntityOptional = fileRepository.findById(data.getFileEntityId());

            if (newFileEntityOptional.isPresent()) {
                profilePD.setFileEntity(newFileEntityOptional.get());
            } else {
                logger.info("FileEntity with ID {} does not exist!", data.getFileEntityId());
            }
        } else if (oldFileEntity != null) {
            fileRepository.delete(oldFileEntity);
            profilePD.setFileEntity(null);
        }
    }

    @Override
    public Page<ProfilePD> getAllByNameContains(String name, Pageable pageable) {
        return profilePDRepository.findAllByName(name, pageable);
    }

    @Override
    public void deleteById(Long id, User currentUser) {
        Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(id);
        Filial workerFilial = optionalProfilePD.get().getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUserFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(workerFilial.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
        }

        if (optionalProfilePD.isPresent()) {
            ProfilePD checkProfileDp = optionalProfilePD.get();
        }
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

    private boolean isUserRestricted(User currentUser, Filial assignedFilial, Filial checkFilial) {
        if (assignedFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            return true;
        }

        if (assignedFilial != null && !assignedFilial.getId().equals(checkFilial.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial ({}) does not match the checkFilial ({})", assignedFilial.getId(), checkFilial.getId());
            return true;
        }

        return false;
    }
}