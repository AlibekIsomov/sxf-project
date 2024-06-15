package com.sxf.project.service.impl;

import com.sxf.project.dto.PurchasingDepartmentDTO;
import com.sxf.project.entity.ProfilePD;
import com.sxf.project.entity.PurchasingDepartment;
import com.sxf.project.repository.ProfilePDRepository;
import com.sxf.project.repository.PurchasingDepartmentRepository;
import com.sxf.project.service.PurchasingDepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PurchasingDepartmentServiceImpl implements PurchasingDepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(PurchasingDepartmentServiceImpl.class);

    @Autowired
    PurchasingDepartmentRepository purchasingDepartmentRepository;

    @Autowired
    ProfilePDRepository profilePDRepository;


    @Override
    public Page<PurchasingDepartment> getAll(Pageable pageable) throws Exception {
        return purchasingDepartmentRepository.findAll(pageable);
    }

    @Override
    public Optional<PurchasingDepartment> getById(Long id) throws Exception {
        if(!purchasingDepartmentRepository.existsById(id)) {
            logger.info("ProfilePD with id " + id + " does not exists");
            return Optional.empty();
        }
        return purchasingDepartmentRepository.findById(id);
    }

    @Override
    public Optional<PurchasingDepartment> create(PurchasingDepartmentDTO data) throws Exception {

        Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(data.getProfileDbId());

        if (!optionalProfilePD.isPresent()) {
            logger.info("Such ID filial does not exist!");
        }

        PurchasingDepartment purchasingDepartment = new PurchasingDepartment();
        purchasingDepartment.setName(data.getName());
        purchasingDepartment.setPrice(data.getPrice());
        purchasingDepartment.setNumber(data.getNumber());
        purchasingDepartment.setPayment(data.getPayment());

        purchasingDepartment.setProfilePD(optionalProfilePD.get());

        return Optional.of(purchasingDepartmentRepository.save(purchasingDepartment));
    }

    @Override
    public Optional<PurchasingDepartment> update(Long id, PurchasingDepartmentDTO data) throws Exception {
        Optional<PurchasingDepartment> optionalPurchasingDepartmentUpdate = purchasingDepartmentRepository.findById(id);

        if (optionalPurchasingDepartmentUpdate.isPresent()) {
            PurchasingDepartment PurchasingDepartmentUpdate = optionalPurchasingDepartmentUpdate.get();

            PurchasingDepartmentUpdate.setName(data.getName());
            PurchasingDepartmentUpdate.setNumber(data.getNumber());
            PurchasingDepartmentUpdate.setNumber(data.getNumber());
            PurchasingDepartmentUpdate.setPayment(data.getPayment());


            // Save the updated
            return Optional.of(purchasingDepartmentRepository.save(PurchasingDepartmentUpdate));
        } else {
            // Handle the case where with the given ID doesn't exist
            throw new ChangeSetPersister.NotFoundException();
        }
    }
    @Override
    public Page<PurchasingDepartment> getAllByNameContains(String name, Pageable pageable) {
        return purchasingDepartmentRepository.findAllByName(name, pageable);
    }

    @Override
    public void deleteById(Long id) {
        if(!purchasingDepartmentRepository.existsById(id)) {
            logger.info("ProfilePD with id " + id + " does not exists");
        };
        purchasingDepartmentRepository.deleteById(id);
    }

}
