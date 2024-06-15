package com.sxf.project.service.impl;


import com.sxf.project.dto.ReportDTO;
import com.sxf.project.entity.FileEntity;
import com.sxf.project.entity.Filial;
import com.sxf.project.entity.Report;
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
import org.springframework.stereotype.Service;

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
    public Optional<Report> getById(Long id) throws Exception {
        if (!reportRepository.existsById(id)) {
            logger.info("Report with id " + id + " does not exists");
            return Optional.empty();
        }
        return reportRepository.findById(id);
    }


    @Override
    public Optional<Report> create(ReportDTO data) throws Exception {

        Optional<Filial> optionalFilial = filialRepository.findById(data.getFilialId());

        if (!optionalFilial.isPresent()) {
            logger.info("Such ID filial does not exist!");
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
    public Optional<Report> update(Long id, ReportDTO data) throws Exception {
        Optional<Filial> optionalFilial = filialRepository.findById(data.getFilialId());

        if (!optionalFilial.isPresent()) {
            logger.info("Such ID filial does not exist!");
            return Optional.empty();
        }

        Optional<Report> exsitingReport = reportRepository.findById(id);

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
    public void deleteById(Long id) {
        if (!reportRepository.existsById(id)) {
            logger.info("Reports with id " + id + " does not exists");
        }
        reportRepository.deleteById(id);
    }

}
