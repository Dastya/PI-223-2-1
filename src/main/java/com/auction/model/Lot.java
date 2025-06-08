package com.auction.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private boolean confirmed;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal startPrice;

    @ManyToOne
    private Category category;

    @ManyToOne
    private User owner;
}