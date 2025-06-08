package com.auction.controller;

import com.auction.dto.LotDTO;
import com.auction.model.Lot;
import com.auction.service.IAuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lots")
@RequiredArgsConstructor
@Tag(name = "Lot Management", description = "Endpoints for managing lots")
public class LotController {
    private final IAuctionService auctionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTERED')")
    @Operation(summary = "Create a new lot")
    @ApiResponse(responseCode = "201", description = "Lot created successfully")
    public ResponseEntity<LotDTO> createLot(@RequestBody LotDTO lotDTO) {
        Lot lot = new Lot();
        lot.setTitle(lotDTO.getTitle());
        lot.setDescription(lotDTO.getDescription());
        lot.setStartPrice(lotDTO.getStartPrice());
        lot.setCategory(auctionService.getCategoryById(lotDTO.getCategoryId()));
        lot.setOwner(auctionService.getUserById(lotDTO.getOwnerId()));
        
        auctionService.addLot(lot);
        return new ResponseEntity<>(convertToDTO(lot), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lot by ID")
    public ResponseEntity<LotDTO> getLot(@PathVariable Long id) {
        Lot lot = auctionService.getLotById(id);
        return lot != null ? ResponseEntity.ok(convertToDTO(lot)) 
                         : ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Get all lots")
    public ResponseEntity<List<LotDTO>> getAllLots() {
        List<LotDTO> lots = auctionService.getAllLots().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lots);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTERED')")
    @Operation(summary = "Update lot")
    public ResponseEntity<LotDTO> updateLot(@PathVariable Long id, @RequestBody LotDTO lotDTO) {
        Lot lot = auctionService.getLotById(id);
        if (lot == null) {
            return ResponseEntity.notFound().build();
        }
        
        lot.setTitle(lotDTO.getTitle());
        lot.setDescription(lotDTO.getDescription());
        lot.setStartPrice(lotDTO.getStartPrice());
        lot.setCategory(auctionService.getCategoryById(lotDTO.getCategoryId()));
        
        auctionService.updateLot(lot);
        return ResponseEntity.ok(convertToDTO(lot));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete lot")
    public ResponseEntity<Void> deleteLot(@PathVariable Long id) {
        Lot lot = auctionService.getLotById(id);
        if (lot == null) {
            return ResponseEntity.notFound().build();
        }
        
        auctionService.deleteLot(id);
        return ResponseEntity.noContent().build();
    }

    private LotDTO convertToDTO(Lot lot) {
        return new LotDTO(
            lot.getId(),
            lot.getTitle(),
            lot.getDescription(),
            lot.getStartPrice(),
            lot.isConfirmed(),
            lot.getCategory().getId(),
            lot.getOwner().getId()
        );
    }
}