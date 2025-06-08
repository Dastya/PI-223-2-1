package com.auction.mapper;

import com.auction.dto.LotDTO;
import com.auction.model.Lot;
import org.springframework.stereotype.Component;

@Component
public class LotMapper {

    public LotDTO toDTO(Lot lot) {
        if (lot == null) return null;
        return new LotDTO(
                lot.getId(),
                lot.getTitle(),
                lot.getDescription(),
                lot.getStartPrice(),
                lot.isConfirmed(),
                lot.getCategory() != null ? lot.getCategory().getId() : null,
                lot.getOwner() != null ? lot.getOwner().getId() : null
        );
    }

    public Lot toEntity(LotDTO dto) {
        if (dto == null) return null;
        Lot lot = new Lot();
        lot.setId(dto.getId());
        lot.setTitle(dto.getTitle());
        lot.setDescription(dto.getDescription());
        lot.setStartPrice(dto.getStartPrice());
        lot.setConfirmed(dto.isConfirmed());
        return lot;
    }
}