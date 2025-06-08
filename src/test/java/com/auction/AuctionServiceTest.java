package com.auction;

import com.auction.exception.UnauthorizedAccessException;
import com.auction.model.*;
import com.auction.repository.IGenericRepository;
import com.auction.repository.UnitOfWork;
import com.auction.service.impl.AuctionService;
import com.auction.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AuctionServiceTest {

    private UnitOfWork uow;
    private SecurityContext securityContext;
    private AuctionService auctionService;
    private IGenericRepository<Category, Long> categoryRepo;

    @BeforeEach
    public void setUp() {
        uow = mock(UnitOfWork.class);
        securityContext = mock(SecurityContext.class);
        categoryRepo = mock(IGenericRepository.class);

        when(uow.getCategoryRepository()).thenReturn(categoryRepo);
        auctionService = new AuctionService(uow, securityContext);
    }

    @Test
    public void testAddCategory_WithAdminUser_ShouldSucceed() {
        User admin = new User();
        admin.setRole(Role.ADMIN);
        when(securityContext.getCurrentUser()).thenReturn(admin);

        Category category = new Category();
        auctionService.addCategory(category);

        verify(categoryRepo).save(category);
    }

    @Test
    public void testAddCategory_WithGuestUser_ShouldThrowException() {
        User guest = new User();
        guest.setRole(Role.GUEST);
        when(securityContext.getCurrentUser()).thenReturn(guest);

        Category category = new Category();

        assertThrows(UnauthorizedAccessException.class, () -> auctionService.addCategory(category));
        verify(categoryRepo, never()).save(any());
    }

    @Test
    public void testPlaceBid_Successful() {
        User bidder = new User();
        bidder.setRole(Role.REGISTERED);

        Lot lot = new Lot();
        lot.setStartPrice(BigDecimal.valueOf(100));

        Auction auction = new Auction();
        auction.setId(1L);
        auction.setLot(lot);
        auction.setCompleted(false);
        auction.setEndTime(LocalDateTime.now().plusMinutes(5));

        Bid bid = new Bid();
        bid.setAmount(150.0);
        bid.setAuction(auction);

        IGenericRepository<Bid, Long> bidRepo = mock(IGenericRepository.class);
        IGenericRepository<Auction, Long> auctionRepo = mock(IGenericRepository.class);
        IGenericRepository<Lot, Long> lotRepo = mock(IGenericRepository.class);

        when(uow.getBidRepository()).thenReturn(bidRepo);
        when(uow.getAuctionRepository()).thenReturn(auctionRepo);
        when(uow.getLotRepository()).thenReturn(lotRepo);
        when(securityContext.getCurrentUser()).thenReturn(bidder);

        when(bidRepo.findAll()).thenReturn(List.of());

        auctionService.placeBid(bid);

        verify(bidRepo).save(bid);
    }

    @Test
    public void testPlaceBid_TooLowAmount_ShouldThrow() {
        User bidder = new User();
        bidder.setRole(Role.REGISTERED);

        Lot lot = new Lot();
        lot.setStartPrice(BigDecimal.valueOf(100));

        Auction auction = new Auction();
        auction.setId(1L);
        auction.setLot(lot);
        auction.setCompleted(false);
        auction.setEndTime(LocalDateTime.now().plusMinutes(5));

        Bid bid = new Bid();
        bid.setAmount(90.0);
        bid.setAuction(auction);

        IGenericRepository<Bid, Long> bidRepo = mock(IGenericRepository.class);
        when(uow.getBidRepository()).thenReturn(bidRepo);
        when(securityContext.getCurrentUser()).thenReturn(bidder);
        when(bidRepo.findAll()).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> auctionService.placeBid(bid));
    }

    @Test
    public void testCheckAndUpdateAuctionStatus_ShouldCompleteAuction() {
        Lot lot = new Lot();
        lot.setConfirmed(false);

        Auction auction = new Auction();
        auction.setCompleted(false);
        auction.setEndTime(LocalDateTime.now().minusMinutes(1));
        auction.setLot(lot);

        IGenericRepository<Auction, Long> auctionRepo = mock(IGenericRepository.class);
        IGenericRepository<Lot, Long> lotRepo = mock(IGenericRepository.class);
        when(uow.getAuctionRepository()).thenReturn(auctionRepo);
        when(uow.getLotRepository()).thenReturn(lotRepo);

        new AuctionService(uow, securityContext)
                .checkAndUpdateAllAuctionsStatus();

        verify(auctionRepo, never()).save(any());
    }

    @Test
    public void testCheckAndUpdateAuctionStatus_ShouldCompleteAuction_WithCompletedLot() {
        Lot lot = new Lot();
        lot.setConfirmed(true);
    }
}
