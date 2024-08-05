package com.sxf.project.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.sxf.project.entity.Role;
import com.sxf.project.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;

@Data
public class UserDTO extends BaseDTO {

    @Pattern(regexp = "^[\\p{L}_' ]*$",message = "Harflardan iborat bo'lishi kerak!")
    @NotBlank(message = "Ism bo'sh masligi kerak!")
    @Size(max = 20)
    private String name;

    @Pattern(regexp = "^[\\p{L}_' ]*$",message = "Harflardan iborat bo'lishi kerak!")
    @NotBlank(message = "Ism bo'sh masligi kerak!")
    @Size(max = 20)
    private String surname;

    @Pattern(regexp = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$",message = "Username faqat harflar yoki avval lotin harflari keyin raqamlardan iborat bo'lishi kerak!")
    @NotBlank(message = "Username bo'sh bo'lmasligi kerak!")
    @Size(min=5,max = 20)
    private String username;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", message = "Yaroqsiz parol kiritildi!")
    @NotBlank(message = "Parol bo'sh bo'lmasligi kerak!")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String phoneNumber;
    @NotBlank(message = "Role bo'sh bo'lmasligi kerak!")
    private Role roles;

    @Setter
    private Boolean active;

    private Long assignedFilialId;

    public UserDTO() {

    }

    public UserDTO(User user) {
        this.name = user.getName();
        this.surname = user.getSurname();
        this.username = user.getUsername();
        this.phoneNumber = user.getPhoneNumber();
        this.roles = user.getRoles();
        this.active = user.getActive();
        if (user.getAssignedFilial() != null) {
            this.assignedFilialId = user.getAssignedFilial().getId();
        } else {
            this.assignedFilialId = null;

        }
    }


}
