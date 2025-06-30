package com.example.order.controller;

import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.service.OrderService;
import com.example.order.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.order.config.NoSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(NoSecurityConfig.class)
@WebMvcTest(
    value = OrderController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfiguration.class)
    }
)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Order testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setProductId(1L);
        testOrderItem.setQuantity(2);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerId(1L);
        testOrder.setStatus("PENDING");
        testOrder.setOrderedDate(LocalDateTime.now());
    }

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
                .andExpect(jsonPath("$[0].customerId").value(1))
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
                .andExpect(jsonPath("$.customerId").value(1))
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
    void testGetOrdersByCustomerId() throws Exception {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrdersByCustomerId(1L)).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/orders/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(orderService, times(1)).getOrdersByCustomerId(1L);
    }

    @Test
    void testCreateOrder() throws Exception {
        // Arrange
        Order orderToCreate = new Order();
        orderToCreate.setCustomerId(1L);
        orderToCreate.setStatus("PENDING");

        when(orderService.createOrder(any(Order.class))).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(1))
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
    void testUpdateOrderStatus_WhenOrderExists() throws Exception {
        // Arrange
        when(orderService.updateOrderStatus(eq(1L), eq("COMPLETED"))).thenReturn(Optional.of(testOrder));

        // Act & Assert
        mockMvc.perform(patch("/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING")); // Original status from testOrder

        verify(orderService, times(1)).updateOrderStatus(eq(1L), eq("COMPLETED"));
    }

    @Test
    void testUpdateOrderStatus_WhenOrderDoesNotExist() throws Exception {
        // Arrange
        when(orderService.updateOrderStatus(eq(999L), eq("COMPLETED"))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(patch("/orders/999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).updateOrderStatus(eq(999L), eq("COMPLETED"));
    }

    @Test
    void testUpdateOrderStatus_WhenExceptionOccurs() throws Exception {
        // Arrange
        when(orderService.updateOrderStatus(1L, "COMPLETED"))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(patch("/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isInternalServerError());
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

    @Test
    void testDeleteOrder_WhenExceptionOccurs() throws Exception {
        // Arrange
        when(orderService.deleteOrder(1L))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetOrdersByStatus() throws Exception {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrdersByStatus("PENDING")).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/orders/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(orderService, times(1)).getOrdersByStatus("PENDING");
    }

    @Test
    void testGetOrderTotalAmount_WhenOrderExists() throws Exception {
        // Arrange
        when(orderService.getOrderTotalAmount(1L)).thenReturn(new BigDecimal("150.00"));

        // Act & Assert
        mockMvc.perform(get("/orders/1/total-amount"))
                .andExpect(status().isOk())
                .andExpect(content().string("150.00"));
    }

    @Test
    void testGetOrderTotalAmount_WhenOrderDoesNotExist() throws Exception {
        // Arrange
        when(orderService.getOrderTotalAmount(999L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/orders/999/total-amount"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetOrderCountByCustomer() throws Exception {
        // Arrange
        when(orderService.getOrderCountByCustomer(1L)).thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/orders/customer/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void testGetDistinctOrderCountByProduct() throws Exception {
        // Arrange
        when(orderService.getDistinctOrderCountByProduct(1L)).thenReturn(3L);

        // Act & Assert
        mockMvc.perform(get("/orders/product/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void testGetCustomerPurchaseCount_AllTime() throws Exception {
        // Arrange
        when(orderService.getCustomerPurchaseCount(1L, null, null)).thenReturn(10L);

        // Act & Assert
        mockMvc.perform(get("/orders/customer/1/purchases/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    void testGetCustomerPurchaseCount_WithDateRange() throws Exception {
        // Arrange
        when(orderService.getCustomerPurchaseCount(1L, "2024-01-01", "2024-12-31")).thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/orders/customer/1/purchases/count")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
} 