package com.sxf.project.service.impl;

import com.sxf.project.dto.FilialDTO;
import com.sxf.project.entity.FileEntity;
import com.sxf.project.entity.Filial;
import com.sxf.project.entity.Role;
import com.sxf.project.entity.User;
import com.sxf.project.repository.FileRepository;
import com.sxf.project.repository.FilialRepository;
import com.sxf.project.repository.UserRepository;
import com.sxf.project.response.ResourceNotFoundException;
import com.sxf.project.service.FilialService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

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
    public Filial assignFilialToManager(Long filialId, Long managerId) throws AccessDeniedException {
        Filial filial = filialRepository.findById(filialId).orElseThrow(() -> new ResourceNotFoundException("Filial not found"));
        User manager = userRepository.findById(managerId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!manager.getRoles().contains(Role.MANAGER)) {
            throw new AccessDeniedException("Access is denied");
        }

        manager.setAssignedFilial(filial);
        userRepository.save(manager);
        return filial;
    }

}
