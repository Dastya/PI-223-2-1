package com.auction.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long lotId;
    private boolean completed;
}