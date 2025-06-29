package com.example.order.controller;

import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

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
        testOrder.setOrderedDate(LocalDateTime.now());

        updatedOrder = new Order();
        updatedOrder.setCustomerId(456L);
        updatedOrder.setStatus("CONFIRMED");
        updatedOrder.setOrderedDate(LocalDateTime.now().minusDays(1));

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
    void testGetAllOrders() throws Exception {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getAllOrders()).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].customerId").value(123))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void testGetOrderById_WhenOrderExists() throws Exception {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(123))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void testGetOrderById_WhenOrderDoesNotExist() throws Exception {
        // Arrange
        when(orderService.getOrderById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrderById(999L);
    }

    @Test
    void testCreateOrder() throws Exception {
        // Arrange
        Order orderToCreate = new Order();
        orderToCreate.setCustomerId(789L);
        orderToCreate.setStatus("PENDING");

        when(orderService.createOrder(any(Order.class))).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(123))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void testCreateOrder_WithInvalidData() throws Exception {
        // Arrange
        Order invalidOrder = new Order();
        // Missing required fields

        when(orderService.createOrder(any(Order.class))).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidOrder)))
                .andExpect(status().isCreated()); // Controller doesn't validate, so it returns 201

        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void testUpdateOrder_WhenOrderExists() throws Exception {
        // Arrange
        when(orderService.updateOrder(eq(1L), any(Order.class))).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(put("/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(123))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService, times(1)).updateOrder(eq(1L), any(Order.class));
    }

    @Test
    void testUpdateOrder_WhenOrderDoesNotExist() throws Exception {
        // Arrange
        when(orderService.updateOrder(eq(999L), any(Order.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/orders/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrder)))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).updateOrder(eq(999L), any(Order.class));
    }

    @Test
    void testDeleteOrder_WhenOrderExists() throws Exception {
        // Arrange
        when(orderService.deleteOrder(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1L);
    }

    @Test
    void testDeleteOrder_WhenOrderDoesNotExist() throws Exception {
        // Arrange
        when(orderService.deleteOrder(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/orders/999"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).deleteOrder(999L);
    }

    // OrderItem Tests
    @Test
    void testGetAllOrderItems() throws Exception {
        // Arrange
        List<OrderItem> orderItems = Arrays.asList(testOrderItem);
        when(orderService.getAllOrderItems()).thenReturn(orderItems);

        // Act & Assert
        mockMvc.perform(get("/orders-items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].productId").value(100))
                .andExpect(jsonPath("$[0].quantity").value(5));

        verify(orderService, times(1)).getAllOrderItems();
    }

    @Test
    void testGetOrderItemsByOrderId() throws Exception {
        // Arrange
        List<OrderItem> orderItems = Arrays.asList(testOrderItem);
        when(orderService.getOrderItemsByOrderId(1L)).thenReturn(orderItems);

        // Act & Assert
        mockMvc.perform(get("/orders-items/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].productId").value(100))
                .andExpect(jsonPath("$[0].quantity").value(5));

        verify(orderService, times(1)).getOrderItemsByOrderId(1L);
    }

    @Test
    void testCreateOrderItem() throws Exception {
        // Arrange
        OrderItem orderItemToCreate = new OrderItem();
        orderItemToCreate.setOrderId(2L);
        orderItemToCreate.setProductId(300L);
        orderItemToCreate.setQuantity(3);

        when(orderService.createOrderItem(any(OrderItem.class))).thenReturn(testOrderItem);

        // Act & Assert
        mockMvc.perform(post("/orders-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderItemToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.productId").value(100))
                .andExpect(jsonPath("$.quantity").value(5));

        verify(orderService, times(1)).createOrderItem(any(OrderItem.class));
    }

    @Test
    void testCreateOrderItem_WithInvalidData() throws Exception {
        // Arrange
        OrderItem invalidOrderItem = new OrderItem();
        // Missing required fields

        when(orderService.createOrderItem(any(OrderItem.class))).thenReturn(testOrderItem);

        // Act & Assert
        mockMvc.perform(post("/orders-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidOrderItem)))
                .andExpect(status().isCreated()); // Controller doesn't validate, so it returns 201

        verify(orderService, times(1)).createOrderItem(any(OrderItem.class));
    }

    @Test
    void testUpdateOrderItem_WhenOrderItemExists() throws Exception {
        // Arrange
        when(orderService.updateOrderItem(eq(1L), any(OrderItem.class))).thenReturn(testOrderItem);

        // Act & Assert
        mockMvc.perform(put("/orders-items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrderItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.productId").value(100))
                .andExpect(jsonPath("$.quantity").value(5));

        verify(orderService, times(1)).updateOrderItem(eq(1L), any(OrderItem.class));
    }

    @Test
    void testUpdateOrderItem_WhenOrderItemDoesNotExist() throws Exception {
        // Arrange
        when(orderService.updateOrderItem(eq(999L), any(OrderItem.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/orders-items/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrderItem)))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).updateOrderItem(eq(999L), any(OrderItem.class));
    }

    @Test
    void testDeleteOrderItem_WhenOrderItemExists() throws Exception {
        // Arrange
        when(orderService.deleteOrderItem(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/orders-items/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrderItem(1L);
    }

    @Test
    void testDeleteOrderItem_WhenOrderItemDoesNotExist() throws Exception {
        // Arrange
        when(orderService.deleteOrderItem(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/orders-items/999"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).deleteOrderItem(999L);
    }

    @Test
    void testGetOrderItemsByOrderId_WhenNoItemsExist() throws Exception {
        // Arrange
        when(orderService.getOrderItemsByOrderId(999L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/orders-items/orders/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(orderService, times(1)).getOrderItemsByOrderId(999L);
    }
} 