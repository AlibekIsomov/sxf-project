package com.sxf.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {

    private String name;

    private Long price;

    private String block;

    private Long floor;

    private Long number;

    private Long square_meters;

    private Long filialId;
}
