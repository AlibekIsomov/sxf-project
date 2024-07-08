package com.sxf.project.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatisticDTO {

    private Long id;
    private Long amount;
    private String type;
    private String classification;
    private String entityName; // New field for the entity name


}
