package com.auction.controller;

import com.auction.dto.CreateUserRequest;
import com.auction.dto.UserDTO;
import com.auction.model.User;
import com.auction.service.IAuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {
    private final IAuctionService auctionService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        
        auctionService.addUser(user);
        return new ResponseEntity<>(convertToDTO(user), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        User user = auctionService.getUserById(id);
        return user != null ? ResponseEntity.ok(convertToDTO(user)) 
                          : ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = auctionService.getAllUsers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody CreateUserRequest request) {
        User user = auctionService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        user.setUsername(request.getUsername());
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setRole(request.getRole());
        
        auctionService.updateUser(user);
        return ResponseEntity.ok(convertToDTO(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = auctionService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        auctionService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
}