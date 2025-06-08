package com.auction.dto;

import com.auction.model.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private Role role;
}