package com.sxf.project.service.impl;

import com.sxf.project.dto.PurchasingDepartmentDTO;
import com.sxf.project.entity.*;
import com.sxf.project.repository.ProfilePDRepository;
import com.sxf.project.repository.PurchasingDepartmentRepository;
import com.sxf.project.service.PurchasingDepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    public Optional<PurchasingDepartment> getById(Long id, User currentUser) throws Exception {
        Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(id);

        if (!optionalProfilePD.isPresent()) {
            logger.info("Such ID filial does not exist!");
            return Optional.empty();
        }

        ProfilePD checkFilial = optionalProfilePD.get();
        Filial filial = checkFilial.getFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUser == null && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUser != null && !currentUser.getId().equals(filial.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
        }

        if(!purchasingDepartmentRepository.existsById(id)) {
            logger.info("ProfilePD with id " + id + " does not exists");
            return Optional.empty();
        }
        return purchasingDepartmentRepository.findById(id);
    }

    @Override
    public Optional<PurchasingDepartment> create(PurchasingDepartmentDTO data, User currentUser) throws Exception {

        Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(data.getProfileDbId());

        if (!optionalProfilePD.isPresent()) {
            logger.info("Such ID filial does not exist!");
            return Optional.empty();
        }

        Filial Filialcheck = optionalProfilePD.get().getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUserFilial == null && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(Filialcheck.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
        }

        PurchasingDepartment purchasingDepartment = new PurchasingDepartment();
        purchasingDepartment.setName(data.getName());
        purchasingDepartment.setPrice(data.getPrice());
        purchasingDepartment.setNumber(data.getNumber());
        purchasingDepartment.setPayment(data.getPayment());
        purchasingDepartment.setDescription(data.getDescription());

        purchasingDepartment.setProfilePD(optionalProfilePD.get());

        return Optional.of(purchasingDepartmentRepository.save(purchasingDepartment));
    }

    @Override
    public Optional<PurchasingDepartment> update(Long id, PurchasingDepartmentDTO data, User currentUser) throws Exception {
        Optional<PurchasingDepartment> optionalPurchasingDepartmentUpdate = purchasingDepartmentRepository.findById(id);
        Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(data.getProfileDbId());

        if (!optionalProfilePD.isPresent()) {
            logger.info("Such ID filial does not exist!");

           return Optional.empty();

        }

        Filial Filialcheck = optionalProfilePD.get().getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUserFilial == null && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUserFilial != null && !currentUserFilial.getId().equals(Filialcheck.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
        }

        if (optionalPurchasingDepartmentUpdate.isPresent()) {

            PurchasingDepartment PurchasingDepartmentUpdate = optionalPurchasingDepartmentUpdate.get();

            PurchasingDepartmentUpdate.setName(data.getName());
            PurchasingDepartmentUpdate.setPrice(data.getPrice());
            PurchasingDepartmentUpdate.setNumber(data.getNumber());
            PurchasingDepartmentUpdate.setPayment(data.getPayment());

            PurchasingDepartmentUpdate.setProfilePD(optionalProfilePD.get());

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
    public Long getTotalFullAmountByProfilePD(Long profilePDId) {
        return purchasingDepartmentRepository.calculateTotalFullAmountByProfilePD(profilePDId);
    }

    @Override
    public Long getRemainingPaymentByProfilePD(Long profilePDId) {
        return purchasingDepartmentRepository.calculateRemainingPaymentByProfilePD(profilePDId);
    }

    @Override
    public void deleteById(Long id, User currentUser) {
        if(!purchasingDepartmentRepository.existsById(id)) {
            logger.info("ProfilePD with id " + id + " does not exists");

            Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(id);
            Filial Filialcheck = optionalProfilePD.get().getFilial();
            Filial currentUserFilial = currentUser.getAssignedFilial();

            // Check if the current user is not assigned to a filial and is not an admin
            if (currentUserFilial == null && !currentUser.getRoles().contains(Role.ADMIN)) {
                logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            }

            // If the current user has an assigned filial, check if it matches the worker's filial
            if (currentUserFilial != null && !currentUserFilial.getId().equals(Filialcheck.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
                logger.info("Restricted: User's assigned filial does not match the worker's filial");
            }
            if (!optionalProfilePD.isPresent()) {
                logger.info("Such ID filial does not exist!");
            }
        };
        purchasingDepartmentRepository.deleteById(id);
    }

}
