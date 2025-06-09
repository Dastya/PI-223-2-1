package com.auction.service;

import com.auction.model.*;
import java.util.List;

public interface IAuctionService {
    // Category
    void addCategory(Category category);
    Category getCategoryById(Long id);
    List<Category> getAllCategories();
    void updateCategory(Category category);
    void deleteCategory(Long id);

    // User
    void addUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(Long id);

    // Lot
    void addLot(Lot lot);
    Lot getLotById(Long id);
    List<Lot> getAllLots();
    void updateLot(Lot lot);
    void deleteLot(Long id);

    // Auction
    void createAuction(Auction auction);
    Auction getAuctionById(Long id);
    List<Auction> getAllAuctions();
    void updateAuction(Auction auction);
    void deleteAuction(Long id);

    // Bid
    void placeBid(Bid bid);
    Bid getBidById(Long id);
    List<Bid> getAllBids();
    List<Bid> getBidsByAuction(Long auctionId);
    void deleteBid(Long id);

    User getUserByUsername(String username);
}