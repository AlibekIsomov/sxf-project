package com.sxf.project.service;

import com.sxf.project.dto.PaymentStatisticDTO;
import com.sxf.project.dto.PaymentStatisticsResponseDTO;

import java.util.Date;
import java.util.List;

public interface PaymentStatisticService {


    List<PaymentStatisticDTO> getPaymentsByFilialId(Long filialId);

    List<PaymentStatisticDTO> getPayments();

    PaymentStatisticsResponseDTO getAllIncome();

    PaymentStatisticsResponseDTO getAllOutcome();

    PaymentStatisticsResponseDTO getIncomeByFilialId(Long filialId);

    PaymentStatisticsResponseDTO getOutcomeByFilialId(Long filialId);

    List<PaymentStatisticDTO> getPaymentsFromToDate(Date fromDate, Date toDate);

    PaymentStatisticsResponseDTO getAllIncomeFromToDate(Date fromDate, Date toDate);

    PaymentStatisticsResponseDTO getAllOutcomeFromToDate(Date fromDate, Date toDate);

    List<PaymentStatisticDTO> getPaymentsByFilialIdFromToDate(Long filialId, Date fromDate, Date toDate);

    PaymentStatisticsResponseDTO getAllIncomeByFilialIdFromToDate(Long filialId, Date fromDate, Date toDate);

    PaymentStatisticsResponseDTO getAllOutcomeByFilialIdFromToDate(Long filialId, Date fromDate, Date toDate);


}