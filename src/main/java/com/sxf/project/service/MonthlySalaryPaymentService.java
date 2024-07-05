package com.sxf.project.service;

import com.sxf.project.dto.MonthlySalaryPaymentDTO;
import com.sxf.project.entity.MonthlySalaryPayment;
import com.sxf.project.entity.User;

import java.util.List;
import java.util.Optional;

public interface MonthlySalaryPaymentService {

    Optional<MonthlySalaryPayment> create(MonthlySalaryPaymentDTO data, User currentUser) throws Exception;

    Optional<MonthlySalaryPayment> update(Long id, MonthlySalaryPaymentDTO data, User currentUser) throws Exception;

    void deletePayment(Long monthlySalaryPaymentsId, User currentUser);

    List<MonthlySalaryPayment> getMonthlySalariesByMonthlySalaryId(Long monthlySalaryId, User currentUser);
}
