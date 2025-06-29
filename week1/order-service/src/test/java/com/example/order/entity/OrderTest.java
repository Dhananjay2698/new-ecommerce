package com.example.order.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class OrderTest {

    private Order order;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        order = new Order();
        testDateTime = LocalDateTime.now();
    }

    @Test
    void testOrderCreation() {
        assertNotNull(order);
        assertNull(order.getId());
        assertNull(order.getCustomerId());
        assertNull(order.getStatus());
        assertNotNull(order.getOrderedDate());
        assertNull(order.getUpdatedDate());
    }

    @Test
    void testIdGetterAndSetter() {
        Long id = 1L;
        order.setId(id);
        assertEquals(id, order.getId());
    }

    @Test
    void testCustomerIdGetterAndSetter() {
        Long customerId = 123L;
        order.setCustomerId(customerId);
        assertEquals(customerId, order.getCustomerId());
    }

    @Test
    void testStatusGetterAndSetter() {
        String status = "PENDING";
        order.setStatus(status);
        assertEquals(status, order.getStatus());
    }

    @Test
    void testStatusWithDifferentValues() {
        String[] statuses = {"PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"};
        
        for (String status : statuses) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
        }
    }

    @Test
    void testOrderedDateGetterAndSetter() {
        order.setOrderedDate(testDateTime);
        assertEquals(testDateTime, order.getOrderedDate());
    }

    @Test
    void testUpdatedDateGetterAndSetter() {
        order.setUpdatedDate(testDateTime);
        assertEquals(testDateTime, order.getUpdatedDate());
    }

    @Test
    void testOrderWithAllFields() {
        Long id = 1L;
        Long customerId = 123L;
        String status = "CONFIRMED";
        LocalDateTime orderedDate = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedDate = LocalDateTime.now();

        order.setId(id);
        order.setCustomerId(customerId);
        order.setStatus(status);
        order.setOrderedDate(orderedDate);
        order.setUpdatedDate(updatedDate);

        assertEquals(id, order.getId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(status, order.getStatus());
        assertEquals(orderedDate, order.getOrderedDate());
        assertEquals(updatedDate, order.getUpdatedDate());
    }

    @Test
    void testDefaultOrderedDate() {
        Order newOrder = new Order();
        assertNotNull(newOrder.getOrderedDate());
        assertTrue(newOrder.getOrderedDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(newOrder.getOrderedDate().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void testOrderWithNullValues() {
        order.setId(null);
        order.setCustomerId(null);
        order.setStatus(null);
        order.setUpdatedDate(null);

        assertNull(order.getId());
        assertNull(order.getCustomerId());
        assertNull(order.getStatus());
        assertNull(order.getUpdatedDate());
        assertNotNull(order.getOrderedDate()); // This should not be null due to default
    }

    @Test
    void testOrderWithZeroValues() {
        order.setId(0L);
        order.setCustomerId(0L);

        assertEquals(0L, order.getId());
        assertEquals(0L, order.getCustomerId());
    }

    @Test
    void testOrderWithEmptyStatus() {
        order.setStatus("");
        assertEquals("", order.getStatus());
    }

    @Test
    void testOrderWithWhitespaceStatus() {
        order.setStatus("   ");
        assertEquals("   ", order.getStatus());
    }
} 