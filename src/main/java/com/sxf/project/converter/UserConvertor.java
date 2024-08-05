package com.sxf.project.converter;

import com.sxf.project.dto.UserDTO;
import com.sxf.project.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserConvertor extends AbstractDTOConverter<User, UserDTO> {
    @Override
    public UserDTO convert(User entity) {
        UserDTO userDTO = new UserDTO();
        populateCommonFields(entity, userDTO);
        userDTO.setName(entity.getName());
        userDTO.setSurname(entity.getSurname());
        userDTO.setUsername(entity.getUsername());
        userDTO.setPhoneNumber(entity.getPhoneNumber());
        userDTO.setRoles(entity.getRoles());
        userDTO.setActive(entity.getActive());
        return userDTO;
    }
}