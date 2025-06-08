package com.auction.mapper;

import com.auction.dto.UserDTO;
import com.auction.model.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getRole());
    }

    public static User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
        return user;
    }
}