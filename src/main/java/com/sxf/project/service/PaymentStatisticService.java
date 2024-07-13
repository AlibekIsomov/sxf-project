package com.sxf.project.service;

import com.sxf.project.dto.PaymentStatisticDTO;
import com.sxf.project.dto.PaymentStatisticsResponseDTO;

import java.util.List;

public interface PaymentStatisticService {


    List<PaymentStatisticDTO> getPaymentsByFilialId(Long filialId);

    List<PaymentStatisticDTO> getPayments();

    PaymentStatisticsResponseDTO getAllIncome();

    PaymentStatisticsResponseDTO getAllOutcome();

    PaymentStatisticsResponseDTO getIncomeByFilialId(Long filialId);

    PaymentStatisticsResponseDTO getOutcomeByFilialId(Long filialId);
}
