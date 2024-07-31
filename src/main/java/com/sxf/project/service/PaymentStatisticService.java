package com.sxf.project.service;

import com.sxf.project.dto.PaymentStatisticDTO;
import com.sxf.project.dto.PaymentStatisticsResponseDTO;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public interface PaymentStatisticService {


    List<PaymentStatisticDTO> getPaymentsByFilialId(Long filialId);

    List<PaymentStatisticDTO> getPayments();

    PaymentStatisticsResponseDTO getAllIncome();

    PaymentStatisticsResponseDTO getAllOutcome();

    PaymentStatisticsResponseDTO getIncomeByFilialId(Long filialId);

    PaymentStatisticsResponseDTO getOutcomeByFilialId(Long filialId);

    List<PaymentStatisticDTO> getPaymentsFromToDate(Instant fromDate, Instant toDate);

    PaymentStatisticsResponseDTO getAllIncomeFromToDate(Instant fromDate, Instant toDate);

    PaymentStatisticsResponseDTO getAllOutcomeFromToDate(Instant fromDate, Instant toDate);

    List<PaymentStatisticDTO> getPaymentsByFilialIdFromToDate(Long filialId, Instant fromDate, Instant toDate);

    PaymentStatisticsResponseDTO getAllIncomeByFilialIdFromToDate(Long filialId, Instant fromDate, Instant toDate);

    PaymentStatisticsResponseDTO getAllOutcomeByFilialIdFromToDate(Long filialId, Instant fromDate, Instant toDate);


}