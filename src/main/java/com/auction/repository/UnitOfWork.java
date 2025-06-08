package com.auction.repository;

import com.auction.model.*;
import com.auction.repository.impl.GenericRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class UnitOfWork {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UnitOfWork(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public UnitOfWork() {}

    public IGenericRepository<User, Long> getUserRepository() {
        return new GenericRepository<>(User.class, entityManager);
    }

    public IGenericRepository<Lot, Long> getLotRepository() {
        return new GenericRepository<>(Lot.class, entityManager);
    }

    public IGenericRepository<Category, Long> getCategoryRepository() {
        return new GenericRepository<>(Category.class, entityManager);
    }

    public IGenericRepository<Auction, Long> getAuctionRepository() {
        return new GenericRepository<>(Auction.class, entityManager);
    }

    public IGenericRepository<Bid, Long> getBidRepository() {
        return new GenericRepository<>(Bid.class, entityManager);
    }
}