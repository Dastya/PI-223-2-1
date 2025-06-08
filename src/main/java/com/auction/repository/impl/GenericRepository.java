package com.auction.repository.impl;

import com.auction.repository.IGenericRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public class GenericRepository<T, ID> implements IGenericRepository<T, ID> {

    @PersistenceContext
    private EntityManager entityManager;

    private Class<T> type;

    public GenericRepository(Class<T> type, EntityManager entityManager) {
        this.type = type;
        this.entityManager = entityManager;
    }

    @Override
    public T save(T entity) {
        return entityManager.merge(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entityManager.find(type, id));
    }

    @Override
    public List<T> findAll() {
        return entityManager.createQuery("FROM " + type.getSimpleName(), type).getResultList();
    }

    @Override
    public void delete(T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    @Override
    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }
}