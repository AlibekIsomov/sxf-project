package com.sxf.project.dto;

import com.sxf.project.entity.UnitOfMeasure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostumerDepartmentDTO {
    private String name;

    private Long price;

    private String description;

    private Long number;

    private UnitOfMeasure unitOfMeasure;

    private Long payment;

    private Long typeOfUnitId;

    private Long profileCDId;

}
