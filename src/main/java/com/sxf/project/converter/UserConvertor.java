package com.sxf.project.converter;



import com.sxf.project.dto.UserDTO;
import com.sxf.project.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserConvertor extends AbstractDTOConverter<User, UserDTO> {
    @Override
    public UserDTO convert(User entity) {
        UserDTO userDTO = new UserDTO(entity);

        super.convert(entity, userDTO);

        return userDTO;
    }
}
