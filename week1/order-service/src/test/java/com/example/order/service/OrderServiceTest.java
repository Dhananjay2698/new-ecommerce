package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private Order updatedOrder;
    private OrderItem testOrderItem;
    private OrderItem updatedOrderItem;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerId(123L);
        testOrder.setStatus("PENDING");
        testOrder.setOrderedDate(LocalDateTime.of(2024, 6, 15, 10, 30));

        updatedOrder = new Order();
        updatedOrder.setCustomerId(456L);
        updatedOrder.setStatus("CONFIRMED");
        updatedOrder.setOrderedDate(LocalDateTime.of(2024, 7, 20, 14, 45));

        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setOrderId(1L);
        testOrderItem.setProductId(100L);
        testOrderItem.setQuantity(5);

        updatedOrderItem = new OrderItem();
        updatedOrderItem.setOrderId(2L);
        updatedOrderItem.setProductId(200L);
        updatedOrderItem.setQuantity(10);
    }

    // Order Tests
    @Test
    void testGetAllOrders() {
        // Arrange
        List<Order> expectedOrders = Arrays.asList(testOrder);
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        // Act
        List<Order> actualOrders = orderService.getAllOrders();

        // Assert
        assertNotNull(actualOrders);
        assertEquals(1, actualOrders.size());
        assertEquals(testOrder, actualOrders.get(0));
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetOrderById_WhenOrderExists() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        // Act
        Optional<Order> result = orderService.getOrderById(orderId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testOrder, result.get());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testGetOrderById_WhenOrderDoesNotExist() {
        // Arrange
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        Optional<Order> result = orderService.getOrderById(orderId);

        // Assert
        assertFalse(result.isPresent());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testCreateOrder() {
        // Arrange
        Order orderToCreate = new Order();
        orderToCreate.setCustomerId(789L);
        orderToCreate.setStatus("PENDING");

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.findByOrderId(any())).thenReturn(Arrays.asList(testOrderItem));
        doNothing().when(emailService).sendOrderConfirmationEmail(any(Order.class), anyList(), anyString());

        // Act
        Order createdOrder = orderService.createOrder(orderToCreate);

        // Assert
        assertNotNull(createdOrder);
        assertEquals(testOrder, createdOrder);
        verify(orderRepository, times(1)).save(orderToCreate);
    }

    @Test
    void testUpdateOrder_WhenOrderExists() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.updateOrder(orderId, updatedOrder);

        // Assert
        assertNotNull(result);
        assertEquals(testOrder, result);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrder_WhenOrderDoesNotExist() {
        // Arrange
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        Order result = orderService.updateOrder(orderId, updatedOrder);

        // Assert
        assertNull(result);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testDeleteOrder_WhenOrderExists() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        doNothing().when(orderRepository).deleteById(orderId);

        // Act
        boolean result = orderService.deleteOrder(orderId);

        // Assert
        assertTrue(result);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void testDeleteOrder_WhenOrderDoesNotExist() {
        // Arrange
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        boolean result = orderService.deleteOrder(orderId);

        // Assert
        assertFalse(result);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void testGetOrdersByCustomerId() {
        // Arrange
        Long customerId = 123L;
        List<Order> expectedOrders = Arrays.asList(testOrder);
        when(orderRepository.findByCustomerId(customerId)).thenReturn(expectedOrders);

        // Act
        List<Order> actualOrders = orderService.getOrdersByCustomerId(customerId);

        // Assert
        assertNotNull(actualOrders);
        assertEquals(1, actualOrders.size());
        assertEquals(testOrder, actualOrders.get(0));
        verify(orderRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    void testGetOrdersByStatus() {
        // Arrange
        String status = "PENDING";
        List<Order> expectedOrders = Arrays.asList(testOrder);
        when(orderRepository.findByStatus(status)).thenReturn(expectedOrders);

        // Act
        List<Order> actualOrders = orderService.getOrdersByStatus(status);

        // Assert
        assertNotNull(actualOrders);
        assertEquals(1, actualOrders.size());
        assertEquals(testOrder, actualOrders.get(0));
        verify(orderRepository, times(1)).findByStatus(status);
    }

    // OrderItem Tests
    @Test
    void testGetAllOrderItems() {
        // Arrange
        List<OrderItem> expectedOrderItems = Arrays.asList(testOrderItem);
        when(orderItemRepository.findAll()).thenReturn(expectedOrderItems);

        // Act
        List<OrderItem> actualOrderItems = orderService.getAllOrderItems();

        // Assert
        assertNotNull(actualOrderItems);
        assertEquals(1, actualOrderItems.size());
        assertEquals(testOrderItem, actualOrderItems.get(0));
        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    void testGetOrderItemsByOrderId() {
        // Arrange
        Long orderId = 1L;
        List<OrderItem> expectedOrderItems = Arrays.asList(testOrderItem);
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(expectedOrderItems);

        // Act
        List<OrderItem> actualOrderItems = orderService.getOrderItemsByOrderId(orderId);

        // Assert
        assertNotNull(actualOrderItems);
        assertEquals(1, actualOrderItems.size());
        assertEquals(testOrderItem, actualOrderItems.get(0));
        verify(orderItemRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void testCreateOrderItem() {
        // Arrange
        OrderItem orderItemToCreate = new OrderItem();
        orderItemToCreate.setOrderId(2L);
        orderItemToCreate.setProductId(300L);
        orderItemToCreate.setQuantity(3);

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // Act
        OrderItem createdOrderItem = orderService.createOrderItem(orderItemToCreate);

        // Assert
        assertNotNull(createdOrderItem);
        assertEquals(testOrderItem, createdOrderItem);
        verify(orderItemRepository, times(1)).save(orderItemToCreate);
    }

    @Test
    void testUpdateOrderItem_WhenOrderItemExists() {
        // Arrange
        Long orderItemId = 1L;
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(testOrderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // Act
        OrderItem result = orderService.updateOrderItem(orderItemId, updatedOrderItem);

        // Assert
        assertNotNull(result);
        assertEquals(testOrderItem, result);
        verify(orderItemRepository, times(1)).findById(orderItemId);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void testUpdateOrderItem_WhenOrderItemDoesNotExist() {
        // Arrange
        Long orderItemId = 999L;
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty());

        // Act
        OrderItem result = orderService.updateOrderItem(orderItemId, updatedOrderItem);

        // Assert
        assertNull(result);
        verify(orderItemRepository, times(1)).findById(orderItemId);
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }

    @Test
    void testDeleteOrderItem_WhenOrderItemExists() {
        // Arrange
        Long orderItemId = 1L;
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(testOrderItem));
        doNothing().when(orderItemRepository).deleteById(orderItemId);

        // Act
        boolean result = orderService.deleteOrderItem(orderItemId);

        // Assert
        assertTrue(result);
        verify(orderItemRepository, times(1)).findById(orderItemId);
        verify(orderItemRepository, times(1)).deleteById(orderItemId);
    }

    @Test
    void testDeleteOrderItem_WhenOrderItemDoesNotExist() {
        // Arrange
        Long orderItemId = 999L;
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty());

        // Act
        boolean result = orderService.deleteOrderItem(orderItemId);

        // Assert
        assertFalse(result);
        verify(orderItemRepository, times(1)).findById(orderItemId);
        verify(orderItemRepository, never()).deleteById(any());
    }

    @Test
    void testUpdateOrder_UpdatesAllFields() {
        // Arrange
        Long orderId = 1L;
        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setCustomerId(100L);
        existingOrder.setStatus("OLD_STATUS");
        existingOrder.setOrderedDate(LocalDateTime.now().minusDays(5));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(orderId);
            return savedOrder;
        });

        // Act
        Order result = orderService.updateOrder(orderId, updatedOrder);

        // Assert
        assertNotNull(result);
        assertEquals(updatedOrder.getCustomerId(), result.getCustomerId());
        assertEquals(updatedOrder.getStatus(), result.getStatus());
        assertEquals(updatedOrder.getOrderedDate(), result.getOrderedDate());
        assertEquals(orderId, result.getId());
    }

    @Test
    void testUpdateOrderItem_UpdatesAllFields() {
        // Arrange
        Long orderItemId = 1L;
        OrderItem existingOrderItem = new OrderItem();
        existingOrderItem.setId(orderItemId);
        existingOrderItem.setOrderId(100L);
        existingOrderItem.setProductId(200L);
        existingOrderItem.setQuantity(1);

        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(existingOrderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem savedOrderItem = invocation.getArgument(0);
            savedOrderItem.setId(orderItemId);
            return savedOrderItem;
        });

        // Act
        OrderItem result = orderService.updateOrderItem(orderItemId, updatedOrderItem);

        // Assert
        assertNotNull(result);
        assertEquals(updatedOrderItem.getOrderId(), result.getOrderId());
        assertEquals(updatedOrderItem.getProductId(), result.getProductId());
        assertEquals(updatedOrderItem.getQuantity(), result.getQuantity());
        assertEquals(orderItemId, result.getId());
    }

    @Test
    void testUpdateOrderStatus_WhenOrderExists() {
        // Arrange
        Long orderId = 1L;
        String newStatus = "COMPLETED";
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        doNothing().when(emailService).sendOrderStatusUpdateEmail(any(Order.class), anyString(), anyString());

        // Act
        Optional<Order> result = orderService.updateOrderStatus(orderId, newStatus);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testOrder, result.get());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrderStatus_WhenOrderDoesNotExist() {
        // Arrange
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        Optional<Order> result = orderService.updateOrderStatus(orderId, "COMPLETED");

        // Assert
        assertFalse(result.isPresent());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    // ========== BUSINESS LOGIC TESTS ==========

    @Test
    void testGetOrderTotalAmount_WhenOrderExists() {
        // Arrange
        Long orderId = 1L;
        List<OrderItem> orderItems = Arrays.asList(testOrderItem);
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(orderItems);
        when(productServiceClient.getProductPrice(testOrderItem.getProductId())).thenReturn(new BigDecimal("25.50"));

        // Act
        BigDecimal totalAmount = orderService.getOrderTotalAmount(orderId);

        // Assert
        assertNotNull(totalAmount);
        assertEquals(new BigDecimal("127.50"), totalAmount); // 25.50 * 5 (quantity from setUp)
        verify(orderItemRepository, times(1)).findByOrderId(orderId);
        verify(productServiceClient, times(1)).getProductPrice(testOrderItem.getProductId());
    }

    @Test
    void testGetOrderTotalAmount_WhenOrderDoesNotExist() {
        // Arrange
        Long orderId = 999L;
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(Arrays.asList());

        // Act
        BigDecimal totalAmount = orderService.getOrderTotalAmount(orderId);

        // Assert
        assertNull(totalAmount);
        verify(orderItemRepository, times(1)).findByOrderId(orderId);
        verify(productServiceClient, never()).getProductPrice(any());
    }

    @Test
    void testGetOrderCountByCustomer() {
        // Arrange
        Long customerId = 123L;
        when(orderRepository.countByCustomerId(customerId)).thenReturn(5L);

        // Act
        Long count = orderService.getOrderCountByCustomer(customerId);

        // Assert
        assertEquals(5L, count);
        verify(orderRepository, times(1)).countByCustomerId(customerId);
    }

    @Test
    void testGetDistinctOrderCountByProduct() {
        // Arrange
        Long productId = 456L;
        OrderItem item1 = new OrderItem();
        item1.setOrderId(1L);
        item1.setProductId(productId);
        item1.setQuantity(2);

        OrderItem item2 = new OrderItem();
        item2.setOrderId(2L);
        item2.setProductId(productId);
        item2.setQuantity(1);

        OrderItem item3 = new OrderItem();
        item3.setOrderId(1L); // Same order as item1
        item3.setProductId(productId);
        item3.setQuantity(3);

        List<OrderItem> orderItems = Arrays.asList(item1, item2, item3);
        when(orderItemRepository.findByProductId(productId)).thenReturn(orderItems);

        // Act
        Long count = orderService.getDistinctOrderCountByProduct(productId);

        // Assert
        assertEquals(2L, count); // Should count distinct order IDs: 1, 2
        verify(orderItemRepository, times(1)).findByProductId(productId);
    }

    @Test
    void testGetCustomerPurchaseCount_AllTime() {
        // Arrange
        Long customerId = 123L;
        List<Order> customerOrders = Arrays.asList(testOrder, updatedOrder);
        when(orderRepository.findByCustomerId(customerId)).thenReturn(customerOrders);

        // Act
        Long count = orderService.getCustomerPurchaseCount(customerId, null, null);

        // Assert
        assertEquals(2L, count);
        verify(orderRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    void testGetCustomerPurchaseCount_WithDateRange() {
        // Arrange
        Long customerId = 123L;
        List<Order> customerOrders = Arrays.asList(testOrder, updatedOrder);
        when(orderRepository.findByCustomerId(customerId)).thenReturn(customerOrders);

        // Act
        Long count = orderService.getCustomerPurchaseCount(customerId, "2024-01-01", "2024-12-31");

        // Assert
        assertEquals(2L, count); // Both orders should be within the date range
        verify(orderRepository, times(1)).findByCustomerId(customerId);
    }
} 