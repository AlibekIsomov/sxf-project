package com.sxf.project.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilialDTO {

    private Long id;

    @NotBlank(message = "Nomi bo'sh bo'lmasligi kerak")
    private String name;

    private String description;

    @Size(min = 6, message = "Content required")
    private String location;

    private long salesDepartment;

    private Long FileEntityId;

    private Set<UserDTO> managers;

}
