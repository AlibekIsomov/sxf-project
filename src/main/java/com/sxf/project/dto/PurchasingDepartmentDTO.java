package com.sxf.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchasingDepartmentDTO {

    private String name;

    private Long price;

    private Long number;

    private String payment;

    private Long filialId;

}
