package com.auction.dto;

import com.auction.model.Role;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String password;
    private Role role;
}
