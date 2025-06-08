// === ENTITY: Bid.java ===
package com.auction.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    private LocalDateTime time;

    @ManyToOne(cascade = CascadeType.MERGE)
    private Auction auction;

    @ManyToOne(cascade = CascadeType.MERGE)
    private User bidder;
}