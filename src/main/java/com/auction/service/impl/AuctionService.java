package com.auction.service.impl;

import com.auction.exception.UnauthorizedAccessException;
import com.auction.model.*;
import com.auction.repository.UnitOfWork;
import com.auction.security.SecurityContext;
import com.auction.service.IAuctionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionService implements IAuctionService {
    private final UnitOfWork uow;
    private final SecurityContext securityContext;

    // Category operations
    @Override
    public void addCategory(Category category) {
        User currentUser = securityContext.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.MANAGER) {
            throw new UnauthorizedAccessException("Only ADMIN and MANAGER can add categories");
        }
        uow.getCategoryRepository().save(category);
    }

    @Override
    public Category getCategoryById(Long id) {
        return uow.getCategoryRepository().findById(id).orElse(null);
    }

    @Override
    public List<Category> getAllCategories() {
        return uow.getCategoryRepository().findAll();
    }

    @Override
    public void updateCategory(Category category) {
        User currentUser = securityContext.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.MANAGER) {
            throw new UnauthorizedAccessException("Only ADMIN and MANAGER can update categories");
        }
        uow.getCategoryRepository().save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        User currentUser = securityContext.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.MANAGER) {
            throw new UnauthorizedAccessException("Only ADMIN and MANAGER can delete categories");
        }
        uow.getCategoryRepository().deleteById(id);
    }

    // User operations
    @Override
    public void addUser(User user) {
        uow.getUserRepository().save(user);
    }

    @Override
    public User getUserById(Long id) {
        return uow.getUserRepository().findById(id).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return uow.getUserRepository().findAll();
    }

    @Override
    public void updateUser(User user) {
        uow.getUserRepository().save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User currentUser = securityContext.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only ADMIN can delete users");
        }
        uow.getUserRepository().deleteById(id);
    }

    // Lot operations
    @Override
    public void addLot(Lot lot) {
        User currentUser = securityContext.getCurrentUser();
        if (currentUser.getRole() == Role.GUEST) {
            throw new UnauthorizedAccessException("Guests cannot add lots");
        }
        uow.getLotRepository().save(lot);
    }

    @Override
    public Lot getLotById(Long id) {
        return uow.getLotRepository().findById(id).orElse(null);
    }

    @Override
    public List<Lot> getAllLots() {
        return uow.getLotRepository().findAll();
    }

    @Override
    public void updateLot(Lot lot) {
        User currentUser = securityContext.getCurrentUser();
        if (currentUser.getRole() == Role.GUEST) {
            throw new UnauthorizedAccessException("Guests cannot update lots");
        }
        uow.getLotRepository().save(lot);
    }

    @Override
    public void deleteLot(Long id) {
        User currentUser = securityContext.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only ADMIN can delete lots");
        }
        uow.getLotRepository().deleteById(id);
    }

    // Auction operations
    @Override
    public void createAuction(Auction auction) {
        User currentUser = securityContext.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only ADMIN can create auctions");
        }
        uow.getAuctionRepository().save(auction);
    }

    @Override
    public Auction getAuctionById(Long id) {
        return uow.getAuctionRepository().findById(id).orElse(null);
    }

    @Override
    public List<Auction> getAllAuctions() {
        return uow.getAuctionRepository().findAll();
    }

    @Override
    public void updateAuction(Auction auction) {
        User currentUser = securityContext.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only ADMIN can update auctions");
        }
        uow.getAuctionRepository().save(auction);
    }

    @Override
    public void deleteAuction(Long id) {
        User currentUser = securityContext.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only ADMIN can delete auctions");
        }
        uow.getAuctionRepository().deleteById(id);
    }

    // Bid operations
    @Override
    public void placeBid(Bid bid) {
        User currentUser = securityContext.getCurrentUser();
        if (currentUser.getRole() != Role.REGISTERED) {
            throw new UnauthorizedAccessException("Only REGISTERED users can place bids");
        }

        Auction auction = bid.getAuction();
        if (auction == null || auction.isCompleted()) {
            throw new IllegalStateException("Auction is not active");
        }

        List<Bid> auctionBids = getBidsByAuction(auction.getId());
        double highestBid = auctionBids.stream()
                .mapToDouble(Bid::getAmount)
                .max()
                .orElse(auction.getLot().getStartPrice().doubleValue());

        if (bid.getAmount() <= highestBid) {
            throw new IllegalArgumentException("Bid amount must be higher than current highest bid");
        }

        bid.setBidder(currentUser);
        uow.getBidRepository().save(bid);

        checkAndUpdateAuctionStatus(auction);
    }

    @Override
    public Bid getBidById(Long id) {
        return uow.getBidRepository().findById(id).orElse(null);
    }

    @Override
    public List<Bid> getAllBids() {
        return uow.getBidRepository().findAll();
    }

    @Override
    public List<Bid> getBidsByAuction(Long auctionId) {
        return uow.getBidRepository().findAll().stream()
                .filter(b -> b.getAuction() != null && b.getAuction().getId().equals(auctionId))
                .toList();
    }

    @Override
    public void deleteBid(Long id) {
        User currentUser = securityContext.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only ADMIN can delete bids");
        }
        uow.getBidRepository().deleteById(id);
    }

    private void checkAndUpdateAuctionStatus(Auction auction) {
        if (LocalDateTime.now().isAfter(auction.getEndTime())) {
            auction.setCompleted(true);
            auction.getLot().setConfirmed(true);
            uow.getAuctionRepository().save(auction);
            uow.getLotRepository().save(auction.getLot());
        }
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void checkAndUpdateAllAuctionsStatus() {
        List<Auction> activeAuctions = getAllAuctions().stream()
                .filter(auction -> !auction.isCompleted())
                .filter(auction -> auction.getEndTime().isBefore(LocalDateTime.now()))
                .toList();

        for (Auction auction : activeAuctions) {
            List<Bid> bids = getBidsByAuction(auction.getId());
            if (!bids.isEmpty()) {
                Bid winningBid = bids.stream()
                        .max(Comparator.comparingDouble(Bid::getAmount))
                        .orElse(null);

                if (winningBid != null) {
                    auction.setCompleted(true);
                    auction.getLot().setConfirmed(true);
                    uow.getAuctionRepository().save(auction);
                    uow.getLotRepository().save(auction.getLot());
                }
            }
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return uow.getUserRepository().findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}