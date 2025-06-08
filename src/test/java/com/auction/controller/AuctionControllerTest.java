package com.auction.controller;

import com.auction.dto.BidDTO;
import com.auction.dto.CategoryDTO;
import com.auction.dto.LotDTO;
import com.auction.model.*;
import com.auction.service.IAuctionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.security.test.context.support.WithMockUser;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest({AuctionController.class,
        BidController.class,
        CategoryController.class,
        LotController.class,
        UserController.class})
@Import(TestSecurityConfig.class)
public class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAuctionService auctionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAuctionById_ShouldReturnAuction() throws Exception {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setLot(new com.auction.model.Lot());
        auction.getLot().setId(2L);
        auction.setStartTime(LocalDateTime.now());
        auction.setEndTime(LocalDateTime.now().plusHours(1));

        when(auctionService.getAuctionById(1L)).thenReturn(auction);

        mockMvc.perform(get("/api/auctions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAuctionById_NotFound() throws Exception {
        when(auctionService.getAuctionById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/auctions/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllAuctions() throws Exception {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setLot(new com.auction.model.Lot());
        auction.getLot().setId(2L);
        auction.setStartTime(LocalDateTime.now());
        auction.setEndTime(LocalDateTime.now().plusHours(1));

        when(auctionService.getAllAuctions()).thenReturn(List.of(auction));

        mockMvc.perform(get("/api/auctions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteAuction_NotFound() throws Exception {
        when(auctionService.getAuctionById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/auctions/1").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteAuction_Success() throws Exception {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setLot(new com.auction.model.Lot());
        auction.getLot().setId(10L);
        auction.setStartTime(LocalDateTime.now());
        auction.setEndTime(LocalDateTime.now().plusHours(1));

        when(auctionService.getAuctionById(1L)).thenReturn(auction);

        mockMvc.perform(delete("/api/auctions/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(auctionService).deleteAuction(1L);
    }

    @Test
    @WithMockUser(roles = "REGISTERED")
    void testPlaceBid_ShouldReturnCreated() throws Exception {
        Bid bid = new Bid();
        bid.setId(1L);
        bid.setAmount(100.0);
        bid.setTime(LocalDateTime.now());
        bid.setAuction(new Auction());
        bid.getAuction().setId(5L);
        bid.setBidder(new User());
        bid.getBidder().setId(3L);

        when(auctionService.getAuctionById(5L)).thenReturn(bid.getAuction());
        when(auctionService.getUserById(3L)).thenReturn(bid.getBidder());

        BidDTO bidDTO = new BidDTO(null, 100.0, null, 5L, 3L);

        mockMvc.perform(post("/api/bids")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bidDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    @WithMockUser
    void testGetBidById_NotFound() throws Exception {
        when(auctionService.getBidById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/bids/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGetAllBids_ShouldReturnList() throws Exception {
        Bid bid = new Bid();
        bid.setId(1L);
        bid.setAmount(99.99);
        bid.setTime(LocalDateTime.now());
        bid.setAuction(new Auction());
        bid.getAuction().setId(2L);
        bid.setBidder(new User());
        bid.getBidder().setId(4L);

        when(auctionService.getAllBids()).thenReturn(List.of(bid));

        mockMvc.perform(get("/api/bids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(99.99));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteBid_Success() throws Exception {
        Bid bid = new Bid();
        bid.setId(1L);
        bid.setAuction(new Auction());
        bid.setBidder(new User());

        when(auctionService.getBidById(1L)).thenReturn(bid);

        mockMvc.perform(delete("/api/bids/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(auctionService).deleteBid(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCategory_ShouldReturnCreated() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Electronics");

        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void testGetCategoryById_NotFound() throws Exception {
        when(auctionService.getCategoryById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGetAllCategories_ShouldReturnList() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Books");

        when(auctionService.getAllCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Books"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategory_Success() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Books");

        when(auctionService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(delete("/api/categories/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(auctionService).deleteCategory(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategory_NotFound() throws Exception {
        when(auctionService.getCategoryById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/categories/1").with(csrf()))
                .andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser(roles = "REGISTERED")
    void testCreateLot_ShouldReturnCreated() throws Exception {
        LotDTO dto = new LotDTO();
        dto.setTitle("MacBook");
        dto.setDescription("MacBook Pro 2023");
        dto.setStartPrice(BigDecimal.valueOf(2000));
        dto.setCategoryId(1L);
        dto.setOwnerId(1L);

        Category category = new Category();
        category.setId(1L);
        User owner = new User();
        owner.setId(1L);

        when(auctionService.getCategoryById(1L)).thenReturn(category);
        when(auctionService.getUserById(1L)).thenReturn(owner);

        mockMvc.perform(post("/api/lots")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void testGetLotById_NotFound() throws Exception {
        when(auctionService.getLotById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/lots/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGetAllLots_ShouldReturnList() throws Exception {
        Lot lot = new Lot();
        lot.setId(1L);
        lot.setTitle("TV");
        lot.setConfirmed(true);
        lot.setStartPrice(BigDecimal.valueOf(1500));
        lot.setOwner(new User());
        lot.getOwner().setId(1L);
        lot.setCategory(new Category());
        lot.getCategory().setId(1L);

        when(auctionService.getAllLots()).thenReturn(List.of(lot));

        mockMvc.perform(get("/api/lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("TV"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteLot_Success() throws Exception {
        Lot lot = new Lot();
        lot.setId(1L);
        lot.setCategory(new Category());
        lot.setOwner(new User());

        when(auctionService.getLotById(1L)).thenReturn(lot);

        mockMvc.perform(delete("/api/lots/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(auctionService).deleteLot(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteLot_NotFound() throws Exception {
        when(auctionService.getLotById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/lots/1").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGetAllUsers_ShouldReturnList() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setRole(Role.ADMIN);

        when(auctionService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"));
    }

    @Test
    @WithMockUser
    void testGetUserById_NotFound() throws Exception {
        when(auctionService.getUserById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser_ShouldReturnNoContent() throws Exception {
        User user = new User();
        user.setId(1L);

        when(auctionService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(delete("/api/users/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(auctionService).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser_NotFound() throws Exception {
        when(auctionService.getUserById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/users/1").with(csrf()))
                .andExpect(status().isNotFound());
    }
}


