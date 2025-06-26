package com.example.customer.repository;

import com.example.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find all customers
    List<Customer> findAll();

    // Find customer by ID
    Optional<Customer> findById(Long id);

    // Delete customer by ID
    void deleteById(Long id);
} 