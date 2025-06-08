package com.auction.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidDTO {
    private Long id;
    private double amount;
    private LocalDateTime time;
    private Long auctionId;
    private Long bidderId;
}
