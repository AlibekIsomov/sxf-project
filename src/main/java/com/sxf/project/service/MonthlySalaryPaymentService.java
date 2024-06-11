package com.sxf.project.service;

import com.sxf.project.dto.MonthlySalaryPaymentDTO;
import com.sxf.project.entity.MonthlySalaryPayment;

import java.util.List;
import java.util.Optional;

public interface MonthlySalaryPaymentService {
    Optional<MonthlySalaryPayment> create(MonthlySalaryPaymentDTO data) throws Exception;

    Optional<MonthlySalaryPayment> update(Long id, MonthlySalaryPaymentDTO data) throws Exception;

    void deletePayment(Long monthlySalaryPaymentsId);

    List<MonthlySalaryPayment> getMonthlySalariesByMonthlySalaryId(Long monthlySalaryId);
}
