package com.sxf.project.service.impl;

import com.sxf.project.dto.PurchasingDepartmentDTO;
import com.sxf.project.entity.*;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.repository.ProfilePDRepository;
import com.sxf.project.repository.PurchasingDepartmentRepository;
import com.sxf.project.service.PurchasingDepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<PurchasingDepartment> getAllByProfileDBId(Long id) {
        if (!profilePDRepository.existsById(id)) {
            throw new IllegalArgumentException("ProfileOD with id " + id + " does not exist");
        }
        return purchasingDepartmentRepository.findAllByProfilePDId(id);
    }

    @Override
    public Optional<PurchasingDepartment> getById(Long id, User currentUser) throws Exception {
        Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(id);
        if (!purchasingDepartmentRepository.existsById(id)) {
            logger.info("ProfilePD with id " + id + " does not exists");
            return Optional.empty();
        }
        if (!optionalProfilePD.isPresent()) {
            logger.info("Such ID filial does not exist!");
            return Optional.empty();
        }

        ProfilePD checkFilial = optionalProfilePD.get();
        Filial filial = checkFilial.getFilial();

        // Check if the current user is not assigned to a filial and is not an admin
        if (currentUser == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
        }

        // If the current user has an assigned filial, check if it matches the worker's filial
        if (currentUser != null && !currentUser.getId().equals(filial.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
        }


        return purchasingDepartmentRepository.findById(id);
    }

    @Override
    public ApiResponse create(PurchasingDepartmentDTO data, User currentUser) {
        Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(data.getProfilePDId());

        if (optionalProfilePD.isEmpty()) {
            logger.info("Such ID ProfilePD does not exist!");
            return new ApiResponse("Bunaqa Idlik ProfilePD yo'q!", false);
        }

        ProfilePD profilePD = optionalProfilePD.get();
        Filial profilePDFilial = profilePD.getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        if (isUserRestricted(currentUser, currentUserFilial, profilePDFilial)) {
            return new ApiResponse("Siz uchun hech qanday filial ulanmagan!", false);
        }

        PurchasingDepartment purchasingDepartment = new PurchasingDepartment();
        purchasingDepartment.setName(data.getName());
        purchasingDepartment.setPrice(data.getPrice());
        purchasingDepartment.setNumber(data.getNumber());
        purchasingDepartment.setPayment(data.getPayment());
        purchasingDepartment.setDescription(data.getDescription());
        purchasingDepartment.setProfilePD(profilePD);

        PurchasingDepartment savedPurchasingDepartment = purchasingDepartmentRepository.save(purchasingDepartment);
        return new ApiResponse("PurchasingDepartment muvaffaqiyatli yaratildi!", true, savedPurchasingDepartment);
    }

    @Override
    public ApiResponse update(Long id, PurchasingDepartmentDTO data, User currentUser) {
        Optional<PurchasingDepartment> optionalPurchasingDepartment = purchasingDepartmentRepository.findById(id);

        if (optionalPurchasingDepartment.isEmpty()) {
            logger.info("Such ID PurchasingDepartment does not exist!");
            return new ApiResponse("Bunaqa Idlik PurchasingDepartment yo'q!", false);
        }

        PurchasingDepartment purchasingDepartment = optionalPurchasingDepartment.get();
        ProfilePD profilePD = purchasingDepartment.getProfilePD();
        Filial profilePDFilial = profilePD.getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        if (isUserRestricted(currentUser, currentUserFilial, profilePDFilial)) {
            return new ApiResponse("Siz uchun hech qanday filial ulanmagan!", false);
        }

        purchasingDepartment.setName(data.getName());
        purchasingDepartment.setPrice(data.getPrice());
        purchasingDepartment.setNumber(data.getNumber());
        purchasingDepartment.setPayment(data.getPayment());
        purchasingDepartment.setDescription(data.getDescription());

        PurchasingDepartment updatedPurchasingDepartment = purchasingDepartmentRepository.save(purchasingDepartment);
        return new ApiResponse("PurchasingDepartment muvaffaqiyatli yangilandi!", true, updatedPurchasingDepartment);
    }

    private boolean isUserRestricted(User currentUser, Filial currentUserFilial, Filial checkFilial) {
        if (currentUserFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            return true;
        }

        if (currentUserFilial != null && !currentUserFilial.getId().equals(checkFilial.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the checkFilial");
            return true;
        }

        return false;
    }

    @Override
    public Page<PurchasingDepartment> getAllByNameContains(String name, Pageable pageable) {
        return purchasingDepartmentRepository.findAllByName(name, pageable);
    }

    @Override
    public Long getTotalPaymentByProfilePD(Long profilePDId) {
        Optional<PurchasingDepartment> optionalPurchasingDepartment = purchasingDepartmentRepository.findById(profilePDId);

        if (!optionalPurchasingDepartment.isPresent()) {
            return null; // Return null if PurchasingDepartment is not found
        }

        PurchasingDepartment purchasingDepartment = optionalPurchasingDepartment.get();
        ProfilePD profilePD = purchasingDepartment.getProfilePD();

        return purchasingDepartmentRepository.calculateTotalFullAmountByProfilePD(profilePD);
    }

    @Override
    public Long getRemainingPaymentByProfilePD(Long profilePDId) {
        Optional<PurchasingDepartment> optionalPurchasingDepartment = purchasingDepartmentRepository.findById(profilePDId);

        if (!optionalPurchasingDepartment.isPresent()) {
            return null;
        }

        PurchasingDepartment purchasingDepartment = optionalPurchasingDepartment.get();

        ProfilePD profilePD = purchasingDepartment.getProfilePD();

        Long totalAmount = purchasingDepartment.getFullAmount();
        Long paidAmount = purchasingDepartmentRepository.calculateTotalPayment(profilePD);

        return totalAmount - paidAmount;
    }

    @Override
    public void deleteById(Long id, User currentUser) {
        if (!purchasingDepartmentRepository.existsById(id)) {
            logger.info("ProfilePD with id " + id + " does not exists");

            Optional<ProfilePD> optionalProfilePD = profilePDRepository.findById(id);
            Filial Filialcheck = optionalProfilePD.get().getFilial();
            Filial currentUserFilial = currentUser.getAssignedFilial();

            // Check if the current user is not assigned to a filial and is not an admin
            if (currentUserFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
                logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            }

            // If the current user has an assigned filial, check if it matches the worker's filial
            if (currentUserFilial != null && !currentUserFilial.getId().equals(Filialcheck.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
                logger.info("Restricted: User's assigned filial does not match the worker's filial");
            }
            if (!optionalProfilePD.isPresent()) {
                logger.info("Such ID filial does not exist!");
            }
        }
        purchasingDepartmentRepository.deleteById(id);
    }

}
