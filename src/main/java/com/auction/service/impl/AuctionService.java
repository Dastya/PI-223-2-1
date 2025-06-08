package com.auction.service.impl;

import com.auction.model.*;
import com.auction.repository.UnitOfWork;
import com.auction.service.IAuctionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionService implements IAuctionService {

    private final UnitOfWork uow;

    // Category
    public void addCategory(Category category) { uow.getCategoryRepository().save(category); }
    public Category getCategoryById(Long id) { return uow.getCategoryRepository().findById(id).orElse(null); }
    public List<Category> getAllCategories() { return uow.getCategoryRepository().findAll(); }
    public void updateCategory(Category category) { uow.getCategoryRepository().save(category); }
    public void deleteCategory(Long id) { uow.getCategoryRepository().deleteById(id); }

    // User
    public void addUser(User user) { uow.getUserRepository().save(user); }
    public User getUserById(Long id) { return uow.getUserRepository().findById(id).orElse(null); }
    public List<User> getAllUsers() { return uow.getUserRepository().findAll(); }
    public void updateUser(User user) { uow.getUserRepository().save(user); }
    public void deleteUser(Long id) { uow.getUserRepository().deleteById(id); }

    // Lot
    public void addLot(Lot lot) { uow.getLotRepository().save(lot); }
    public Lot getLotById(Long id) { return uow.getLotRepository().findById(id).orElse(null); }
    public List<Lot> getAllLots() { return uow.getLotRepository().findAll(); }
    public void updateLot(Lot lot) { uow.getLotRepository().save(lot); }
    public void deleteLot(Long id) { uow.getLotRepository().deleteById(id); }

    // Auction
    public void createAuction(Auction auction) { uow.getAuctionRepository().save(auction); }
    public Auction getAuctionById(Long id) { return uow.getAuctionRepository().findById(id).orElse(null); }
    public List<Auction> getAllAuctions() { return uow.getAuctionRepository().findAll(); }
    public void updateAuction(Auction auction) { uow.getAuctionRepository().save(auction); }
    public void deleteAuction(Long id) { uow.getAuctionRepository().deleteById(id); }

    // Bid
    public void placeBid(Bid bid) { uow.getBidRepository().save(bid); }
    public Bid getBidById(Long id) { return uow.getBidRepository().findById(id).orElse(null); }
    public List<Bid> getAllBids() { return uow.getBidRepository().findAll(); }
    public List<Bid> getBidsByAuction(Long auctionId) {
        return uow.getBidRepository().findAll().stream()
                .filter(b -> b.getAuction() != null && b.getAuction().getId().equals(auctionId))
                .toList();
    }
    public void deleteBid(Long id) { uow.getBidRepository().deleteById(id); }
}
