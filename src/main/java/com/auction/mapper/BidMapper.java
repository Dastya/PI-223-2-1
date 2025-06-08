package com.auction.mapper;

import com.auction.dto.BidDTO;
import com.auction.model.Bid;

public class BidMapper {
    public static BidDTO toDTO(Bid bid) {
        return new BidDTO(
                bid.getId(),
                bid.getAmount(),
                bid.getTime(),
                bid.getAuction() != null ? bid.getAuction().getId() : null,
                bid.getBidder() != null ? bid.getBidder().getId() : null
        );
    }

    public static Bid toEntity(BidDTO dto) {
        Bid bid = new Bid();
        bid.setId(dto.getId());
        bid.setAmount(dto.getAmount());
        bid.setTime(dto.getTime());
        return bid;
    }
}