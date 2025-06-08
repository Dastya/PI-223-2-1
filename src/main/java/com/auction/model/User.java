package com.auction.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "bidder")
    private List<Bid> bids;
}