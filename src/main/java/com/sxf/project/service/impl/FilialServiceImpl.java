package com.sxf.project.service.impl;


import com.sxf.project.dto.FilialDTO;
import com.sxf.project.dto.UserDTO;
import com.sxf.project.entity.FileEntity;
import com.sxf.project.entity.Filial;
import com.sxf.project.entity.Role;
import com.sxf.project.entity.User;
import com.sxf.project.repository.FileRepository;
import com.sxf.project.repository.FilialRepository;
import com.sxf.project.repository.UserRepository;
import com.sxf.project.service.FilialService;
import jakarta.transaction.Transactional;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilialServiceImpl implements FilialService {
    private static final Logger logger = LoggerFactory.getLogger(FilialServiceImpl.class);

    @Autowired
    FilialRepository filialRepository;

    @Autowired
    UserRepository userRepository;

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
    public List<Filial> getAllManagers(User user) {
        if (user.getAssignedFilial() != null && user.getAssignedFilial().getId() != null) {
            return filialRepository.findAllById(Collections.singleton(user.getAssignedFilial().getId()));
        } else {
            logger.info("User is not assigned to any filial");
            ResponseEntity.badRequest().body("Siz uchun hech qanday filial bog'lanmagan!");
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Filial> create(FilialDTO data) throws Exception {

        Filial filial = new Filial();
        filial.setName(data.getName());
        filial.setDescription(data.getDescription());

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
    public ByteArrayInputStream exportExcel() throws IOException {
        List<Filial> profiles = filialRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Profiles");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Description");
        headerRow.createCell(3).setCellValue("Created At");

        int rowIdx = 1;
        for (Filial profile : profiles) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(profile.getId());
            row.createCell(1).setCellValue(profile.getName());
            row.createCell(2).setCellValue(profile.getDescription());
            row.createCell(3).setCellValue(profile.getCreatedAt().toString());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    @Override
    public void deleteById(Long id) {
        if (!filialRepository.existsById(id)) {
            logger.info("Filial with id " + id + " does not exists");
        }
        filialRepository.deleteById(id);
    }

    @Override
    public Filial getFilialForUser(Long filialId, Long userId) throws AccessDeniedException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRoles().contains(Role.ADMIN)) {
            return filialRepository.findById(filialId).orElseThrow(() -> new ResourceNotFoundException("Filial not found"));
        }

        if (user.getRoles().contains(Role.MANAGER) && user.getAssignedFilial() != null && user.getAssignedFilial().getId().equals(filialId)) {
            return user.getAssignedFilial();
        }

        throw new AccessDeniedException("Access is denied");
    }

    @Override
    public List<Filial> getAllFilialsForAdmin(Long adminId) throws AccessDeniedException {
        User admin = userRepository.findById(adminId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!admin.getRoles().contains(Role.ADMIN)) {
            throw new AccessDeniedException("Access is denied");
        }

        return filialRepository.findAll();
    }


    @Override
    @Transactional
    public FilialDTO assignFilialToManager(Long filialId, Long managerId) throws AccessDeniedException {
        Filial filial = filialRepository.findByIdWithManagers(filialId)
                .orElseThrow(() -> new ResourceNotFoundException("Filial not found"));
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!manager.getRoles().contains(Role.MANAGER)) {
            throw new AccessDeniedException("Access is denied");
        }

        // Unassign the manager from any existing filial if necessary
        if (manager.getAssignedFilial() != null) {
            manager.getAssignedFilial().getManagers().remove(manager);
        }

        // Assign the manager to the new filial and update the relationship
        manager.setAssignedFilial(filial);
        filial.getManagers().add(manager);

        userRepository.save(manager); // Save the manager
        filialRepository.save(filial); // Save the filial

        return convertToDTO(filial);
    }

    @Override
    @Transactional
    public FilialDTO unassignFilialFromManager(Long managerId) throws AccessDeniedException {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!manager.getRoles().contains(Role.MANAGER)) {
            throw new AccessDeniedException("Access is denied");
        }

        Filial assignedFilial = manager.getAssignedFilial();
        if (assignedFilial == null) {
            throw new ResourceNotFoundException("Manager is not assigned to any filial");
        }

        // Unassign the manager from the filial
        assignedFilial.getManagers().remove(manager);
        manager.setAssignedFilial(null);

        userRepository.save(manager); // Save the manager
        filialRepository.save(assignedFilial); // Save the filial

        return convertToDTO(assignedFilial);
    }

    private FilialDTO convertToDTO(Filial filial) {
        FilialDTO filialDTO = new FilialDTO();
        filialDTO.setId(filial.getId());

        Set<UserDTO> managerDTOs = filial.getManagers().stream()
                .map(manager -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setId(manager.getId());
                    userDTO.setAssignedFilialId(filial.getId());
                    return userDTO;
                }).collect(Collectors.toSet());

        filialDTO.setManagers(managerDTOs);
        return filialDTO;
    }

    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String message) {
            super(message);
        }
    }

}
