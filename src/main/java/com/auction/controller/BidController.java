package com.auction.controller;

import com.auction.dto.BidDTO;
import com.auction.model.Bid;
import com.auction.service.IAuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
@Tag(name = "Bid Management", description = "Endpoints for managing bids")
public class BidController {
    private final IAuctionService auctionService;

    @PostMapping
    @PreAuthorize("hasRole('REGISTERED')")
    @Operation(summary = "Place a new bid")
    @ApiResponse(responseCode = "201", description = "Bid placed successfully")
    public ResponseEntity<BidDTO> placeBid(@RequestBody BidDTO bidDTO) {
        Bid bid = new Bid();
        bid.setAmount(bidDTO.getAmount());
        bid.setTime(LocalDateTime.now());
        bid.setAuction(auctionService.getAuctionById(bidDTO.getAuctionId()));
        bid.setBidder(auctionService.getUserById(bidDTO.getBidderId()));
        
        auctionService.placeBid(bid);
        return new ResponseEntity<>(convertToDTO(bid), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bid by ID")
    public ResponseEntity<BidDTO> getBid(@PathVariable Long id) {
        Bid bid = auctionService.getBidById(id);
        return bid != null ? ResponseEntity.ok(convertToDTO(bid)) 
                         : ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Get all bids")
    public ResponseEntity<List<BidDTO>> getAllBids() {
        List<BidDTO> bids = auctionService.getAllBids().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bids);
    }

    @GetMapping("/auction/{auctionId}")
    @Operation(summary = "Get all bids for an auction")
    public ResponseEntity<List<BidDTO>> getBidsByAuction(@PathVariable Long auctionId) {
        List<BidDTO> bids = auctionService.getBidsByAuction(auctionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bids);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete bid")
    public ResponseEntity<Void> deleteBid(@PathVariable Long id) {
        Bid bid = auctionService.getBidById(id);
        if (bid == null) {
            return ResponseEntity.notFound().build();
        }
        
        auctionService.deleteBid(id);
        return ResponseEntity.noContent().build();
    }

    private BidDTO convertToDTO(Bid bid) {
        return new BidDTO(
            bid.getId(),
            bid.getAmount(),
            bid.getTime(),
            bid.getAuction().getId(),
            bid.getBidder().getId()
        );
    }
}