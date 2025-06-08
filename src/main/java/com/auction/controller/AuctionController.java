package com.auction.controller;

import com.auction.dto.AuctionDTO;
import com.auction.model.Auction;
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
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@Tag(name = "Auction Management", description = "Endpoints for managing auctions")
public class AuctionController {
    private final IAuctionService auctionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new auction")
    @ApiResponse(responseCode = "201", description = "Auction created successfully")
    public ResponseEntity<AuctionDTO> createAuction(@RequestBody AuctionDTO auctionDTO) {
        Auction auction = new Auction();
        auction.setStartTime(auctionDTO.getStartTime());
        auction.setEndTime(auctionDTO.getEndTime());
        auction.setLot(auctionService.getLotById(auctionDTO.getLotId()));
        auction.setCompleted(false);
        
        auctionService.createAuction(auction);
        return new ResponseEntity<>(convertToDTO(auction), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get auction by ID")
    public ResponseEntity<AuctionDTO> getAuction(@PathVariable Long id) {
        Auction auction = auctionService.getAuctionById(id);
        return auction != null ? ResponseEntity.ok(convertToDTO(auction)) 
                             : ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Get all auctions")
    public ResponseEntity<List<AuctionDTO>> getAllAuctions() {
        List<AuctionDTO> auctions = auctionService.getAllAuctions().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(auctions);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update auction")
    public ResponseEntity<AuctionDTO> updateAuction(@PathVariable Long id, @RequestBody AuctionDTO auctionDTO) {
        Auction auction = auctionService.getAuctionById(id);
        if (auction == null) {
            return ResponseEntity.notFound().build();
        }
        
        auction.setStartTime(auctionDTO.getStartTime());
        auction.setEndTime(auctionDTO.getEndTime());
        auction.setCompleted(auctionDTO.isCompleted());
        
        auctionService.updateAuction(auction);
        return ResponseEntity.ok(convertToDTO(auction));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete auction")
    public ResponseEntity<Void> deleteAuction(@PathVariable Long id) {
        Auction auction = auctionService.getAuctionById(id);
        if (auction == null) {
            return ResponseEntity.notFound().build();
        }
        
        auctionService.deleteAuction(id);
        return ResponseEntity.noContent().build();
    }

    private AuctionDTO convertToDTO(Auction auction) {
        AuctionDTO dto = new AuctionDTO();
        dto.setId(auction.getId());
        dto.setStartTime(auction.getStartTime());
        dto.setEndTime(auction.getEndTime());
        dto.setLotId(auction.getLot().getId());
        dto.setCompleted(auction.isCompleted());
        return dto;
    }
}