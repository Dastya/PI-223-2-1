package com.auction.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotDTO {
    private Long id;
    private String title;
    private String description;
    private boolean confirmed;
    private Long categoryId;
    private Long ownerId;
}
