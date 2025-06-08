package com.auction.mapper;

import com.auction.dto.AuctionDTO;
import com.auction.model.Auction;
import org.springframework.stereotype.Component;

@Component
public class AuctionMapper {

    public AuctionDTO toDTO(Auction auction) {
        if (auction == null) return null;
        return new AuctionDTO(
                auction.getId(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getLot() != null ? auction.getLot().getId() : null,
                auction.isCompleted()
        );
    }

    public Auction toEntity(AuctionDTO dto) {
        if (dto == null) return null;
        Auction auction = new Auction();
        auction.setId(dto.getId());
        auction.setStartTime(dto.getStartTime());
        auction.setEndTime(dto.getEndTime());
        auction.setCompleted(dto.isCompleted());
        return auction;
    }
}