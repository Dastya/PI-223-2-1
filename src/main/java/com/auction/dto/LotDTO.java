package com.auction.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal startPrice;
    private boolean confirmed;
    private Long categoryId;
    private Long ownerId;
}