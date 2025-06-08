package com.auction.repository;

import java.util.List;
import java.util.Optional;

public interface IGenericRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(T entity);
    void deleteById(ID id);
}