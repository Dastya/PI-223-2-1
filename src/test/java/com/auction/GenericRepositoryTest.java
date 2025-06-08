package com.auction;

import com.auction.model.User;
import com.auction.repository.impl.GenericRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GenericRepositoryTest {

    private EntityManager entityManager;
    private GenericRepository<User, Long> userRepository;

    @BeforeEach
    public void setUp() {
        entityManager = mock(EntityManager.class);
        userRepository = new GenericRepository<>(User.class, entityManager);
    }

    @Test
    public void testSave() {
        User user = new User();
        when(entityManager.merge(user)).thenReturn(user);

        User result = userRepository.save(user);

        assertEquals(user, result);
        verify(entityManager).merge(user);
    }

    @Test
    public void testFindById() {
        User user = new User();
        when(entityManager.find(User.class, 1L)).thenReturn(user);

        Optional<User> result = userRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    public void testFindAll() {
        User user1 = new User();
        User user2 = new User();
        List<User> users = List.of(user1, user2);

        // Мокаем запрос
        TypedQuery<User> query = mock(TypedQuery.class);
        when(entityManager.createQuery("FROM User", User.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(users);

        List<User> result = userRepository.findAll();

        assertEquals(2, result.size());
        assertEquals(user1, result.get(0));
    }


    @Test
    public void testDelete() {
        User user = new User();
        when(entityManager.contains(user)).thenReturn(true);

        userRepository.delete(user);

        verify(entityManager).remove(user);
    }

    @Test
    public void testDeleteById() {
        User user = new User();
        when(entityManager.find(User.class, 1L)).thenReturn(user);
        when(entityManager.contains(user)).thenReturn(true);

        userRepository.deleteById(1L);

        verify(entityManager).remove(user);
    }
}
