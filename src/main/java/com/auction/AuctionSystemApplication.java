package com.auction;

import com.auction.exception.UnauthorizedAccessException;
import com.auction.model.*;
import com.auction.service.IAuctionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@EnableScheduling
@SpringBootApplication
public class AuctionSystemApplication {
    // Store user references for testing
    private User admin;
    private User manager;
    private User registered;
    private User guest;

    public static void main(String[] args) {
        SpringApplication.run(AuctionSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(IAuctionService auctionService) {
        return args -> {
            try {
                setupUsers(auctionService);
                testCategories(auctionService);
                testAuctionsAndBids(auctionService);
                testLotConfirmation(auctionService);
                printStatistics(auctionService);
            } catch (Exception e) {
                System.err.println("Error during initialization: " + e.getMessage());
            }
        };
    }

    private void setupUsers(IAuctionService auctionService) {
        admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setRole(Role.ADMIN);
        auctionService.addUser(admin);

        manager = new User();
        manager.setUsername("manager");
        manager.setPassword("manager123");
        manager.setRole(Role.MANAGER);
        auctionService.addUser(manager);

        registered = new User();
        registered.setUsername("user");
        registered.setPassword("user123");
        registered.setRole(Role.REGISTERED);
        auctionService.addUser(registered);

        guest = new User();
        guest.setUsername("guest");
        guest.setPassword("guest123");
        guest.setRole(Role.GUEST);
        auctionService.addUser(guest);
    }

    private void testCategories(IAuctionService auctionService) {
        System.out.println("\nTesting category creation:");
        
        // Admin creates category
        setCurrentUser(admin);
        Category electronics = new Category();
        electronics.setName("Electronics");
        auctionService.addCategory(electronics);
        System.out.println("Admin successfully created category");

        // Manager creates category
        setCurrentUser(manager);
        Category books = new Category();
        books.setName("Books");
        auctionService.addCategory(books);
        System.out.println("Manager successfully created category");

        // Registered user tries to create category
        try {
            setCurrentUser(registered);
            Category furniture = new Category();
            furniture.setName("Furniture");
            auctionService.addCategory(furniture);
        } catch (UnauthorizedAccessException e) {
            System.out.println("Registered user failed to create category (expected): " + e.getMessage());
        }
    }

    private void testAuctionsAndBids(IAuctionService auctionService) {
        System.out.println("\nTesting auction creation and bidding:");
        
        try {
            setCurrentUser(admin);
            List<Category> categories = auctionService.getAllCategories();
            Category electronics = categories.isEmpty() ? null : categories.get(0);
            
            if (electronics != null) {
                Lot iphone = new Lot();
                iphone.setTitle("iPhone 14");
                iphone.setDescription("New, unopened");
                iphone.setStartPrice(BigDecimal.valueOf(1200.0));
                iphone.setCategory(electronics);
                iphone.setOwner(admin);
                auctionService.addLot(iphone);

                Auction auction = new Auction();
                auction.setLot(iphone);
                auction.setStartTime(LocalDateTime.now());
                auction.setEndTime(LocalDateTime.now().plusDays(1));
                auctionService.createAuction(auction);
                System.out.println("Admin successfully created auction");

                // Registered user places bid
                setCurrentUser(registered);
                Bid bid1 = new Bid();
                bid1.setAmount(1250.0);
                bid1.setTime(LocalDateTime.now());
                bid1.setAuction(auction);
                bid1.setBidder(registered);
                auctionService.placeBid(bid1);
                System.out.println("Registered user successfully placed bid");
            } else {
                System.out.println("No categories found to create auction");
            }
        } catch (Exception e) {
            System.out.println("Error during auction and bid test: " + e.getMessage());
        }
    }

    private void testLotConfirmation(IAuctionService auctionService) {
        System.out.println("\nTesting lot confirmation:");
        try {
            setCurrentUser(registered);
            List<Auction> auctions = auctionService.getAllAuctions();
            if (!auctions.isEmpty()) {
                Auction auction = auctions.get(0);
                Bid winningBid = new Bid();
                winningBid.setAmount(1500.0);
                winningBid.setTime(LocalDateTime.now());
                winningBid.setAuction(auction);
                winningBid.setBidder(registered);
                auctionService.placeBid(winningBid);

                auction.setEndTime(LocalDateTime.now().minusMinutes(1));
                auctionService.updateAuction(auction);
                
                Lot lot = auction.getLot();
                System.out.println("Lot confirmation status: " + lot.isConfirmed());
            } else {
                System.out.println("No auctions found for confirmation test");
            }
        } catch (Exception e) {
            System.out.println("Error during lot confirmation test: " + e.getMessage());
        }
    }

    private void printStatistics(IAuctionService auctionService) {
        System.out.println("\nFinal statistics:");
        System.out.println("Users: " + auctionService.getAllUsers().size());
        System.out.println("Categories: " + auctionService.getAllCategories().size());
        System.out.println("Lots: " + auctionService.getAllLots().size());
        System.out.println("Auctions: " + auctionService.getAllAuctions().size());
        System.out.println("Bids: " + auctionService.getAllBids().size());
    }

    private void setCurrentUser(User user) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList())
        );
    }
}