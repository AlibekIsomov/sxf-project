package com.sxf.project.service.impl;


import com.sxf.project.dto.CostumerDepartmentDTO;
import com.sxf.project.entity.*;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.repository.CostumerDepartmentRepository;
import com.sxf.project.repository.ProfileCDRepository;
import com.sxf.project.repository.TypeOfUnitRepository;
import com.sxf.project.service.CostumerDepartmentService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CostumerDepartmentServiceImpl implements CostumerDepartmentService {
    private static final Logger logger = LoggerFactory.getLogger(CostumerDepartmentServiceImpl.class);

    @Autowired
    CostumerDepartmentRepository costumerDepartmentRepository;

    @Autowired
    ProfileCDRepository profileCDRepository;

    @Autowired
    TypeOfUnitRepository typeOfUnitRepository;


    @Override
    public Page<CostumerDepartment> getAll(Pageable pageable) throws Exception {
        return costumerDepartmentRepository.findAll(pageable);
    }

    @Override
    public List<CostumerDepartment> getAllByProfileCDId(Long id) {
        if (!profileCDRepository.existsById(id)) {
            throw new IllegalArgumentException("ProfileCD with id " + id + " does not exist");
        }
        return costumerDepartmentRepository.findAllByProfileCDId(id);
    }

    @Override
    public Optional<CostumerDepartment> getById(Long id, User currentUser) throws Exception {
        Optional<ProfileCD> optionalProfilePD = profileCDRepository.findById(id);
        if (!costumerDepartmentRepository.existsById(id)) {
            logger.info("ProfilePD with id " + id + " does not exists");
            return Optional.empty();
        }
        if (!optionalProfilePD.isPresent()) {
            logger.info("Such ID filial does not exist!");
            return Optional.empty();
        }

        ProfileCD checkFilial = optionalProfilePD.get();
        Filial filial = checkFilial.getFilial();

        if (currentUser == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
        }

        if (currentUser != null && !currentUser.getId().equals(filial.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial does not match the worker's filial");
        }


        return costumerDepartmentRepository.findById(id);
    }

    @Override
    public ApiResponse create(CostumerDepartmentDTO data, User currentUser) throws Exception {
        Optional<ProfileCD> optionalProfileCD = profileCDRepository.findById(data.getProfileCDId());
        Optional<TypeOfUnit> optionalTypeOfUnit = typeOfUnitRepository.findById(data.getTypeOfUnitId());

        if (optionalProfileCD.isEmpty()) {
            logger.info("ProfileCD with ID {} does not exist!", data.getProfileCDId());
            return new ApiResponse("Bunaqa Idlik ProfileCD yo'q!", false);
        }

        if (optionalTypeOfUnit.isEmpty()) {
            logger.info("TypeOfUnit with ID {} does not exist!", data.getTypeOfUnitId());
            return new ApiResponse("Bunaqa Idlik o'lchov turi yo'q!", false);
        }

        ProfileCD profileCD = optionalProfileCD.get();
        Filial filialCheck = profileCD.getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        if (isUserRestricted(currentUser, currentUserFilial, filialCheck)) {
            return new ApiResponse("Siz uchun hech qanday filial ulanmagan!", false);
        }

        CostumerDepartment costumerDepartment = buildCostumerDepartment(data, profileCD, optionalTypeOfUnit.get());
        costumerDepartmentRepository.save(costumerDepartment);

        return new ApiResponse("Jarayon muvaffaqiyatli bajarildi!", true, costumerDepartment);
    }

    @Override
    public ApiResponse update(Long id, CostumerDepartmentDTO data, User currentUser) throws Exception {
        Optional<CostumerDepartment> optionalCostumerDepartment = costumerDepartmentRepository.findById(id);

        Optional<ProfileCD> optionalProfileCD = profileCDRepository.findById(data.getProfileCDId());

        Optional<TypeOfUnit> optionalTypeOfUnit = typeOfUnitRepository.findById(data.getTypeOfUnitId());

        if (optionalCostumerDepartment.isEmpty()) {
            logger.info("Such ID filial does not exist!");
            return new ApiResponse("Bunaqa Idlik ProfileCD yo'q!", false);
        }

        if (optionalProfileCD.isEmpty()) {
            logger.info("Such ID filial does not exist!");
            return new ApiResponse("Bunaqa Idlik ProfileCD yo'q!", false);
        }

        if (optionalTypeOfUnit.isEmpty()) {
            logger.info("TypeOfUnit with ID {} does not exist!", data.getTypeOfUnitId());
            return new ApiResponse("Bunaqa Idlik o'lchov turi yo'q!", false);
        }

        ProfileCD profileCD = optionalProfileCD.get();
        Filial filialCheck = profileCD.getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        if (isUserRestricted(currentUser, currentUserFilial, filialCheck)) {
            return new ApiResponse("Siz uchun hech qanday filial ulanmagan!", false);
        }

        CostumerDepartment costumerDepartment = buildCostumerDepartment(data, profileCD, optionalTypeOfUnit.get());
        costumerDepartmentRepository.save(costumerDepartment);

        return new ApiResponse("Jarayon muvaffaqiyatli bajarildi!", true, costumerDepartment);

    }

    @Override
    public Page<CostumerDepartment> getAllByNameContains(String name, Pageable pageable) {
        return costumerDepartmentRepository.findAllByName(name, pageable);
    }

    @Override
    public Long getTotalFullAmountByProfileCD(Long profileCDId) {
        CostumerDepartment costumerDepartment = costumerDepartmentRepository.findById(profileCDId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with id: " + profileCDId));

        ProfileCD profileCD = costumerDepartment.getProfileCD();

        return costumerDepartmentRepository.calculateTotalFullAmountByProfileCD(profileCD);
    }

    @Override
    public Long getRemainingPaymentByProfileCD(Long profileCDId) {
        CostumerDepartment costumerDepartment = costumerDepartmentRepository.findById(profileCDId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found for id: " + profileCDId));

        ProfileCD profileCD = costumerDepartment.getProfileCD();

        Long totalAmount = costumerDepartment.getFullAmount();
        Long paidAmount = costumerDepartmentRepository.calculateTotalPayment(profileCD);

        return totalAmount - paidAmount;
    }

    @Override
    public ApiResponse deleteById(Long id, User currentUser) {
        if (!costumerDepartmentRepository.existsById(id)) {
            logger.info("CostumerPurchasment with id {} does not exist", id);
            return new ApiResponse("Bunaqa Idlik CostumerPurchasment yo'q!", false);
        }

        Optional<ProfileCD> optionalProfileCD = profileCDRepository.findById(id);

        if (optionalProfileCD.isEmpty()) {
            logger.info("ProfileCD with id {} does not exist", id);
            return new ApiResponse("Bunaqa Idlik ProfileCD yo'q!", false);
        }

        ProfileCD profileCD = optionalProfileCD.get();
        Filial filialCheck = profileCD.getFilial();
        Filial currentUserFilial = currentUser.getAssignedFilial();

        if (isUserRestricted(currentUser, currentUserFilial, filialCheck)) {
            return new ApiResponse("Siz uchun hech qanday filial ulanmagan!", false);
        }

        try {
            costumerDepartmentRepository.deleteById(id);
            logger.info("Successfully deleted CostumerPurchasment with id {}", id);
            return new ApiResponse("Jarayon muvaffaqiyatli bajarildi!", true);
        } catch (Exception e) {
            logger.error("Error deleting CostumerPurchasment with id {}", id, e);
            return new ApiResponse("Jarayonni bajarishda xatolik yuz berdi!", false);
        }
    }

    private CostumerDepartment buildCostumerDepartment(CostumerDepartmentDTO data, ProfileCD profileCD, TypeOfUnit typeOfUnit) {
        CostumerDepartment costumerDepartment = new CostumerDepartment();
        costumerDepartment.setName(data.getName());
        costumerDepartment.setPrice(data.getPrice());
        costumerDepartment.setNumber(data.getNumber());
        costumerDepartment.setPayment(data.getPayment());
        costumerDepartment.setUnitOfMeasure(data.getUnitOfMeasure());
        costumerDepartment.setDescription(data.getDescription());
        costumerDepartment.setTypeOfUnit(typeOfUnit);
        costumerDepartment.setProfileCD(profileCD);

        return costumerDepartment;
    }

    private boolean isUserRestricted(User currentUser, Filial currentUserFilial, Filial filialCheck) {
        if (currentUserFilial == null && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User does not have an assigned filial and is not an ADMIN");
            return true;
        }

        if (currentUserFilial != null && !currentUserFilial.getId().equals(filialCheck.getId()) && !currentUser.getRoles().equals(Role.ADMIN)) {
            logger.info("Restricted: User's assigned filial ({}) does not match the filial ({})", currentUserFilial.getId(), filialCheck.getId());
            return true;
        }

        return false;
    }
}
