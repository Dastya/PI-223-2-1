package com.auction.mapper;

import com.auction.dto.LotDTO;
import com.auction.model.Lot;

public class LotMapper {
    public static LotDTO toDTO(Lot lot) {
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

    public static Lot toEntity(LotDTO dto) {
        Lot lot = new Lot();
        lot.setId(dto.getId());
        lot.setTitle(dto.getTitle());
        lot.setDescription(dto.getDescription());
        lot.setStartPrice(dto.getStartPrice());
        lot.setConfirmed(dto.isConfirmed());
        return lot;
    }
}