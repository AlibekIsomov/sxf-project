package com.sxf.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatisticsResponseDTO {
    private List<PaymentStatisticDTO> payments;
    private double total;
}
