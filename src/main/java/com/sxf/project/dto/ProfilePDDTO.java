package com.sxf.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePDDTO {
    private Long id;

    private String name;

    private String description;

    private Long FileEntityId;

    private Long filialId;

}
