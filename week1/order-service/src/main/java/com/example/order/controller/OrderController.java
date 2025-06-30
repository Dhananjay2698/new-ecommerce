package com.example.order.controller;

import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.service.OrderService;
import com.example.order.dto.OrderStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    // ========== ORDER ENDPOINTS ==========
    
    // Get all orders
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    // Get order by ID
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    // Get orders by customer ID
    @GetMapping("/orders/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }
    
    // Get orders by status
    @GetMapping("/orders/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    
    // Create new order
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }
    
    // Update order
    @PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        Order updatedOrder = orderService.updateOrder(id, orderDetails);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.notFound().build();
    }
    
    // Update order status
    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatusRequest request) {
        Optional<Order> updatedOrder = orderService.updateOrderStatus(id, request.getStatus());
        return updatedOrder.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
    
    // Delete order
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        boolean deleted = orderService.deleteOrder(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // ========== BUSINESS LOGIC ENDPOINTS ==========
    
    // Get sum of total amount for a given order
    @GetMapping("/orders/{id}/total-amount")
    public ResponseEntity<BigDecimal> getOrderTotalAmount(@PathVariable Long id) {
        BigDecimal totalAmount = orderService.getOrderTotalAmount(id);
        if (totalAmount != null) {
            return ResponseEntity.ok(totalAmount);
        }
        return ResponseEntity.notFound().build();
    }
    
    // Count Number of Orders from a particular customer
    @GetMapping("/orders/customer/{customerId}/count")
    public ResponseEntity<Long> getOrderCountByCustomer(@PathVariable Long customerId) {
        Long count = orderService.getOrderCountByCustomer(customerId);
        return ResponseEntity.ok(count);
    }
    
    // Count Distinct orders in which a given Product Appears
    @GetMapping("/orders/product/{productId}/count")
    public ResponseEntity<Long> getDistinctOrderCountByProduct(@PathVariable Long productId) {
        Long count = orderService.getDistinctOrderCountByProduct(productId);
        return ResponseEntity.ok(count);
    }
    
    // Count total purchases made by customer till date or for a given date range
    @GetMapping("/orders/customer/{customerId}/purchases/count")
    public ResponseEntity<Long> getCustomerPurchaseCount(
            @PathVariable Long customerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Long count = orderService.getCustomerPurchaseCount(customerId, startDate, endDate);
        return ResponseEntity.ok(count);
    }
    
    // ========== ORDER ITEM ENDPOINTS ==========
    
    // Get all order items
    @GetMapping("/orders-items")
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        List<OrderItem> orderItems = orderService.getAllOrderItems();
        return ResponseEntity.ok(orderItems);
    }
    
    // Get order items by order ID
    @GetMapping("/orders-items/orders/{id}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrderId(@PathVariable("id") Long id) {
        List<OrderItem> orderItems = orderService.getOrderItemsByOrderId(id);
        return ResponseEntity.ok(orderItems);
    }
    
    // Create order item
    @PostMapping("/orders-items")
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItem orderItem) {
        OrderItem createdOrderItem = orderService.createOrderItem(orderItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrderItem);
    }
    
    // Update order item
    @PutMapping("/orders-items/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id, @RequestBody OrderItem orderItemDetails) {
        OrderItem updatedOrderItem = orderService.updateOrderItem(id, orderItemDetails);
        if (updatedOrderItem != null) {
            return ResponseEntity.ok(updatedOrderItem);
        }
        return ResponseEntity.notFound().build();
    }
    
    // Delete order item
    @DeleteMapping("/orders-items/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        boolean deleted = orderService.deleteOrderItem(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
} 