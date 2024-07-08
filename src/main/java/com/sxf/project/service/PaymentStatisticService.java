package com.sxf.project.service;

import com.sxf.project.dto.PaymentStatisticDTO;

import java.util.List;

public interface PaymentStatisticService {

    List<PaymentStatisticDTO> getPaymentsByFilialId(Long filialId);
}
