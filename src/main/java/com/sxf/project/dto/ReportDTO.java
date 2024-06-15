package com.sxf.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {

    private Long id;

    private String name;

    private Long price;

    private String block;

    private Long floor;

    private Long number;

    private Long square_meters;

    private Long FileEntityId;

    private List<ReportPaymentDTO> payments;

    private Long filialId;
}
