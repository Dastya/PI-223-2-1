package com.auction.mapper;

import com.auction.dto.UserDTO;
import com.auction.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public UserDTO toDTO(User user) {
        if (user == null) return null;
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
        return user;
    }
}