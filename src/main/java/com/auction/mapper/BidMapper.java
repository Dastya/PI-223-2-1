package com.auction.mapper;

import com.auction.dto.BidDTO;
import com.auction.model.Bid;
import org.springframework.stereotype.Component;

@Component
public class BidMapper {

    public BidDTO toDTO(Bid bid) {
        if (bid == null) return null;
        return new BidDTO(
                bid.getId(),
                bid.getAmount(),
                bid.getTime(),
                bid.getAuction() != null ? bid.getAuction().getId() : null,
                bid.getBidder() != null ? bid.getBidder().getId() : null
        );
    }

    public Bid toEntity(BidDTO dto) {
        if (dto == null) return null;
        Bid bid = new Bid();
        bid.setId(dto.getId());
        bid.setAmount(dto.getAmount());
        bid.setTime(dto.getTime());
        return bid;
    }
}