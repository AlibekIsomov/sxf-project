package com.sxf.project.service.impl;


import com.sxf.project.dto.ReportDTO;
import com.sxf.project.entity.*;
import com.sxf.project.repository.FileRepository;
import com.sxf.project.repository.FilialRepository;
import com.sxf.project.repository.ReportPaymentRepository;
import com.sxf.project.repository.ReportRepository;
import com.sxf.project.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportPaymentRepository reportPaymentRepository;

    @Autowired
    private FilialRepository filialRepository;

    @Autowired
    private FileRepository fileRepository;

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Override
    public Page<Report> getAll(Pageable pageable) throws Exception {
        return reportRepository.findAll(pageable);
    }

    @Override
    public List<Report> getReportByFilial(User user) {
        if (user.getAssignedFilial() != null && user.getAssignedFilial().getId() != null) {
            return reportRepository.findAllByFilialId(user.getAssignedFilial().getId());
        } else {
            logger.info("User is not assigned to any filial");
            ResponseEntity.badRequest().body("Siz uchun hech qanday filial bog'lanmagan!");
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Report> getById(Long id, User currentUser) throws Exception {

        Optional<Report> exsitingReport = reportRepository.findById(id);

        if (!exsitingReport.isPresent()) {
            logger.info("Such ID filial does not exist!");
            return Optional.empty();
        }

        Report checkWorker = exsitingReport.get();

        Filial currentUserFilial = currentUser.getAssignedFilial();

        if (currentUserFilial == null) {
            if (!currentUser.getRoles().contains(Role.ADMIN)) {
                logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
                return Optional.empty();
            }
        } else {
            // If the current user has an assigned filial, check if it matches the checkFilial
            if (!currentUserFilial.getId().equals(checkWorker.getFilial().getId())) {
                logger.info("Restricted: User's assigned filial does not match the checkFilial");
                return Optional.empty();
            }
        }

        return reportRepository.findById(id);
    }

    @Override
    public List<Report> getAllByFilial(Long id) {
        return reportRepository.findAllByFilialId(id);
    }


    @Override
    public Optional<Report> create(ReportDTO data, User currentUser) throws Exception {

        Optional<Filial> optionalFilial = filialRepository.findById(data.getFilialId());

        if (!optionalFilial.isPresent()) {
            logger.info("Such ID filial does not exist!");
            Optional.empty();
        }
            Filial checkFilial = optionalFilial.get();

            Filial assignedFilial = currentUser.getAssignedFilial();
            if (assignedFilial == null) {
                if (!currentUser.getRoles().contains(Role.ADMIN)) {
                    logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
                    return Optional.empty();
                }
            } else {
                // If the current user has an assigned filial, check if it matches the checkFilial
                if (!assignedFilial.getId().equals(checkFilial.getId())) {
                    logger.info("Restricted: User's assigned filial does not match the checkFilial");
                    return Optional.empty();
                }
            }




        Report report = new Report();
        report.setName(data.getName());
        report.setPrice(data.getPrice());
        report.setBlock(data.getBlock());
        report.setFloor(data.getFloor());
        report.setNumber(data.getNumber());
        report.setSquare_meters(data.getSquare_meters());

        report.setFilial(optionalFilial.get());

        return Optional.of(reportRepository.save(report));
    }

    @Override
    public Optional<Report> update(Long id, ReportDTO data, User currentUser) throws Exception {
        Optional<Filial> optionalFilial = filialRepository.findById(data.getFilialId());

        if (!optionalFilial.isPresent()) {
            logger.info("Such ID filial does not exist!");
            return Optional.empty();
        }

        Optional<Report> exsitingReport = reportRepository.findById(id);

        if (exsitingReport.isPresent()) {
            Report checkreport = exsitingReport.get();
            Filial checkFilial = optionalFilial.get();

            Filial assignedFilial = currentUser.getAssignedFilial();
            if (assignedFilial == null) {
                if (!currentUser.getRoles().contains(Role.ADMIN)) {
                    logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
                    return Optional.empty();
                }
            } else {
                // If the current user has an assigned filial, check if it matches the checkFilial
                if (!assignedFilial.getId().equals(checkFilial.getId())) {
                    logger.info("Restricted: User's assigned filial does not match the checkFilial");
                    return Optional.empty();
                }
            }
        }

        if (!exsitingReport.isPresent()) {
            logger.info("Report with id " + id + " does not exist");
            return Optional.empty();
        }

        Report reportToUpdate = exsitingReport.get();

        FileEntity oldFileEntity = reportToUpdate.getFileEntity();

        // Check if fileId is provided before removing the FileEntity
        if (data.getFileEntityId() != null) {
            Optional<FileEntity> newFileEntityOptional = fileRepository.findById(data.getFileEntityId());

            if (!newFileEntityOptional.isPresent()) {
                logger.info("FileEntity with id " + data.getFileEntityId() + " does not exist");
                return Optional.empty();
            }

            // Set the new FileEntity
            FileEntity newFileEntity = newFileEntityOptional.get();
            reportToUpdate.setFileEntity(newFileEntity);
        } else {
            // If fileId is not provided, remove the old FileEntity
            if (oldFileEntity != null) {
                // Delete the old FileEntity from the repository
                fileRepository.delete(oldFileEntity);
                // Remove the old FileEntity from the Store
                reportToUpdate.setFileEntity(null);
            }
        }

        // Update the Store entity with the new data
        reportToUpdate.setName(data.getName());
        reportToUpdate.setPrice(data.getPrice());
        reportToUpdate.setBlock(data.getBlock());
        reportToUpdate.setFloor(data.getFloor());
        reportToUpdate.setNumber(data.getNumber());
        reportToUpdate.setSquare_meters(data.getSquare_meters());

        return Optional.of(reportRepository.save(reportToUpdate));
    }

    @Override
    public void deleteById(Long id, User currentUser) {
        Optional<Report> existingReport = reportRepository.findById(id);

        if (existingReport.isPresent()) {
            Filial workerFilial = existingReport.get().getFilial();
            Filial currentUserFilial = currentUser.getAssignedFilial();

            // Check if the current user is not assigned to a filial and is not an admin
            if (currentUserFilial == null && !currentUser.getRoles().contains(Role.ADMIN)) {
                logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
                return; // Stop further execution
            }

            // If the current user has an assigned filial, check if it matches the worker's filial
            if (currentUserFilial != null && !currentUserFilial.getId().equals(workerFilial.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
                logger.info("Restricted: User's assigned filial does not match the worker's filial");
                return; // Stop further execution
            }

            reportRepository.deleteById(id);
        }
    }

}
