package com.sxf.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilialDTO {

    private Long id;

    private String name;

    private String description;

    private Long FileEntityId;

    private Set<UserDTO> managers;

}
