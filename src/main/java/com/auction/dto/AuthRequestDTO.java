package com.auction.dto;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String username;
    private String password;
}