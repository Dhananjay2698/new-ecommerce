package com.example.order.repository;

import com.example.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Find all orders
    List<Order> findAll();

    // Find order by ID
    Optional<Order> findById(Long id);

    // Find orders by customer ID
    List<Order> findByCustomerId(Long customerId);

    // Find orders by status
    List<Order> findByStatus(String status);

    // Count orders by customer ID
    long countByCustomerId(Long customerId);

    // Delete order by ID
    void deleteById(Long id);
} 