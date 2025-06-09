package com.auction.controller;

import com.auction.dto.AuthRequestDTO;
import com.auction.dto.RegisterRequestDTO;
import com.auction.model.Role;
import com.auction.model.User;
import com.auction.service.IAuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final IAuctionService auctionService;

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok().body("User logged in successfully");
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        if (auctionService.getUserByUsername(request.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        user.setRole(Role.REGISTERED);

        auctionService.addUser(user);
        return ResponseEntity.ok().body("User registered successfully");
    }
}