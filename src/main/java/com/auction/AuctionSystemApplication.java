package com.auction;

import com.auction.model.*;
import com.auction.service.IAuctionService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

@EnableScheduling
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Auction System API",
        version = "1.0",
        description = "API for managing auctions, lots, bids, and users",
        contact = @Contact(
            name = "Admin",
            email = "admin@auction.com"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "http://www.apache.org/licenses/LICENSE-2.0"
        )
    ),
    security = @SecurityRequirement(name = "basicAuth")
)
public class AuctionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionSystemApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("basicAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")
                )
            );
    }

    @Bean
    public CommandLineRunner demo(IAuctionService auctionService, PasswordEncoder passwordEncoder) {
        return args -> {
            if (auctionService.getAllUsers().isEmpty()) {
                initializeDemoData(auctionService, passwordEncoder);
            }
        };
    }

    private void initializeDemoData(IAuctionService auctionService, PasswordEncoder passwordEncoder) {
        try {
            // Create admin user first
            User admin = createUser("admin", "admin123", Role.ADMIN, passwordEncoder);
            auctionService.addUser(admin);

            // Set up security context with admin credentials
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                admin.getUsername(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + admin.getRole()))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Create other users
            User manager = createUser("manager", "manager123", Role.MANAGER, passwordEncoder);
            User registeredUser = createUser("user", "user123", Role.REGISTERED, passwordEncoder);
            auctionService.addUser(manager);
            auctionService.addUser(registeredUser);

            // Create categories
            Category electronics = new Category();
            electronics.setName("Electronics");
            auctionService.addCategory(electronics);

            Category books = new Category();
            books.setName("Books");
            auctionService.addCategory(books);

            // Create lots and auctions
            createSampleLotAndAuction(auctionService, electronics, admin, "iPhone 14", "New, unopened", 1200.0);
            createSampleLotAndAuction(auctionService, books, admin, "Rare Book Collection", "First editions", 500.0);
        } finally {
            // Clear security context after initialization
            SecurityContextHolder.clearContext();
        }
    }

    private User createUser(String username, String password, Role role, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return user;
    }

    private void createSampleLotAndAuction(
            IAuctionService auctionService, 
            Category category, 
            User owner, 
            String title, 
            String description, 
            double startPrice) {
        
        Lot lot = new Lot();
        lot.setTitle(title);
        lot.setDescription(description);
        lot.setStartPrice(BigDecimal.valueOf(startPrice));
        lot.setCategory(category);
        lot.setOwner(owner);
        auctionService.addLot(lot);

        Auction auction = new Auction();
        auction.setLot(lot);
        auction.setStartTime(LocalDateTime.now());
        auction.setEndTime(LocalDateTime.now().plusDays(7));
        auction.setCompleted(false);
        auctionService.createAuction(auction);
    }
}