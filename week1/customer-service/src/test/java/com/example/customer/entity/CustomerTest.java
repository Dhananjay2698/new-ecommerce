package com.example.customer.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class CustomerTest {

    private Customer customer;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        testDateTime = LocalDateTime.now();
    }

    @Test
    void testCustomerCreation() {
        assertNotNull(customer);
        assertNull(customer.getId());
        assertNull(customer.getName());
        assertNull(customer.getEmail());
        assertNotNull(customer.getCreatedDate());
        assertNull(customer.getUpdatedDate());
    }

    @Test
    void testIdGetterAndSetter() {
        Long id = 1L;
        customer.setId(id);
        assertEquals(id, customer.getId());
    }

    @Test
    void testNameGetterAndSetter() {
        String name = "John Doe";
        customer.setName(name);
        assertEquals(name, customer.getName());
    }

    @Test
    void testEmailGetterAndSetter() {
        String email = "john.doe@example.com";
        customer.setEmail(email);
        assertEquals(email, customer.getEmail());
    }

    @Test
    void testCreatedDateGetterAndSetter() {
        customer.setCreatedDate(testDateTime);
        assertEquals(testDateTime, customer.getCreatedDate());
    }

    @Test
    void testUpdatedDateGetterAndSetter() {
        customer.setUpdatedDate(testDateTime);
        assertEquals(testDateTime, customer.getUpdatedDate());
    }

    @Test
    void testCustomerWithAllFields() {
        Long id = 1L;
        String name = "Jane Smith";
        String email = "jane.smith@example.com";
        LocalDateTime createdDate = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedDate = LocalDateTime.now();

        customer.setId(id);
        customer.setName(name);
        customer.setEmail(email);
        customer.setCreatedDate(createdDate);
        customer.setUpdatedDate(updatedDate);

        assertEquals(id, customer.getId());
        assertEquals(name, customer.getName());
        assertEquals(email, customer.getEmail());
        assertEquals(createdDate, customer.getCreatedDate());
        assertEquals(updatedDate, customer.getUpdatedDate());
    }

    @Test
    void testDefaultCreatedDate() {
        Customer newCustomer = new Customer();
        assertNotNull(newCustomer.getCreatedDate());
        assertTrue(newCustomer.getCreatedDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(newCustomer.getCreatedDate().isAfter(LocalDateTime.now().minusSeconds(1)));
    }
} 