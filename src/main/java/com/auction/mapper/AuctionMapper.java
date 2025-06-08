package com.auction.mapper;

import com.auction.dto.AuctionDTO;
import com.auction.model.Auction;

public class AuctionMapper {
    public static AuctionDTO toDTO(Auction auction) {
        return new AuctionDTO(
                auction.getId(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getLot() != null ? auction.getLot().getId() : null,
                auction.isCompleted()
        );
    }

    public static Auction toEntity(AuctionDTO dto) {
        Auction auction = new Auction();
        auction.setId(dto.getId());
        auction.setStartTime(dto.getStartTime());
        auction.setEndTime(dto.getEndTime());
        auction.setCompleted(dto.isCompleted());
        return auction;
    }
}