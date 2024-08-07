package com.sxf.project.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpdateDTO {
    private Long id;

    @Pattern(regexp = "^[\\p{L}_' ]*$",message = "Harflardan iborat bo'lishi kerak!")
    @NotBlank(message = "Ism bo'sh bulmasligi kerak")
    @Size(max=20)
    private String name;

    @Pattern(regexp = "^[\\p{L}_' ]*$",message = "Harflardan iborat bo'lishi kerak!")
    @NotBlank(message = "Ism bo'sh bulmasligi kerak")
    @Size(max=20)
    private String surname;

    @Pattern(regexp = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$",message = "Username faqat harflar yoki avval lotin harflari keyin raqamlardan iborat bo'lishi kerak!")
    @Size(max = 30, min = 6)
    private String username;

    private String phoneNumber;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", message = "Yaroqsiz parol kiritildi!")
    @NotBlank(message = "Parol bo'sh bo'lmasligi kerak!")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String oldPassword;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", message = "Yaroqsiz parol kiritildi!")
    @NotBlank(message = "Parol bo'sh bo'lmasligi kerak!")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;

    private String confirm;
}
