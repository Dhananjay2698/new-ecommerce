package com.example.order.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderItem = new OrderItem();
    }

    @Test
    void testOrderItemCreation() {
        assertNotNull(orderItem);
        assertNull(orderItem.getId());
        assertNull(orderItem.getOrderId());
        assertNull(orderItem.getProductId());
        assertNull(orderItem.getQuantity());
    }

    @Test
    void testIdGetterAndSetter() {
        Long id = 1L;
        orderItem.setId(id);
        assertEquals(id, orderItem.getId());
    }

    @Test
    void testOrderIdGetterAndSetter() {
        Long orderId = 123L;
        orderItem.setOrderId(orderId);
        assertEquals(orderId, orderItem.getOrderId());
    }

    @Test
    void testProductIdGetterAndSetter() {
        Long productId = 456L;
        orderItem.setProductId(productId);
        assertEquals(productId, orderItem.getProductId());
    }

    @Test
    void testQuantityGetterAndSetter() {
        Integer quantity = 5;
        orderItem.setQuantity(quantity);
        assertEquals(quantity, orderItem.getQuantity());
    }

    @Test
    void testQuantityWithDifferentValues() {
        Integer[] quantities = {0, 1, 10, 100, 999};
        
        for (Integer quantity : quantities) {
            orderItem.setQuantity(quantity);
            assertEquals(quantity, orderItem.getQuantity());
        }
    }

    @Test
    void testQuantityWithNegativeValues() {
        Integer negativeQuantity = -5;
        orderItem.setQuantity(negativeQuantity);
        assertEquals(negativeQuantity, orderItem.getQuantity());
    }

    @Test
    void testOrderItemWithAllFields() {
        Long id = 1L;
        Long orderId = 123L;
        Long productId = 456L;
        Integer quantity = 3;

        orderItem.setId(id);
        orderItem.setOrderId(orderId);
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);

        assertEquals(id, orderItem.getId());
        assertEquals(orderId, orderItem.getOrderId());
        assertEquals(productId, orderItem.getProductId());
        assertEquals(quantity, orderItem.getQuantity());
    }

    @Test
    void testOrderItemWithNullValues() {
        orderItem.setId(null);
        orderItem.setOrderId(null);
        orderItem.setProductId(null);
        orderItem.setQuantity(null);

        assertNull(orderItem.getId());
        assertNull(orderItem.getOrderId());
        assertNull(orderItem.getProductId());
        assertNull(orderItem.getQuantity());
    }

    @Test
    void testOrderItemWithZeroValues() {
        orderItem.setId(0L);
        orderItem.setOrderId(0L);
        orderItem.setProductId(0L);
        orderItem.setQuantity(0);

        assertEquals(0L, orderItem.getId());
        assertEquals(0L, orderItem.getOrderId());
        assertEquals(0L, orderItem.getProductId());
        assertEquals(0, orderItem.getQuantity());
    }

    @Test
    void testOrderItemWithLargeValues() {
        Long largeId = Long.MAX_VALUE;
        Long largeOrderId = 999999999L;
        Long largeProductId = 888888888L;
        Integer largeQuantity = Integer.MAX_VALUE;

        orderItem.setId(largeId);
        orderItem.setOrderId(largeOrderId);
        orderItem.setProductId(largeProductId);
        orderItem.setQuantity(largeQuantity);

        assertEquals(largeId, orderItem.getId());
        assertEquals(largeOrderId, orderItem.getOrderId());
        assertEquals(largeProductId, orderItem.getProductId());
        assertEquals(largeQuantity, orderItem.getQuantity());
    }

    @Test
    void testOrderItemWithSameOrderAndProductIds() {
        Long sameId = 100L;
        orderItem.setOrderId(sameId);
        orderItem.setProductId(sameId);

        assertEquals(sameId, orderItem.getOrderId());
        assertEquals(sameId, orderItem.getProductId());
    }
} 