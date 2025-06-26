package com.example.order.controller;

import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
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
    
    // Delete order
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        boolean deleted = orderService.deleteOrder(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
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
    
    // ========== ORDER ITEM ENDPOINTS ==========
    
    // Get all order items
    @GetMapping("/order-items")
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        List<OrderItem> orderItems = orderService.getAllOrderItems();
        return ResponseEntity.ok(orderItems);
    }
    
    // Get order items by order ID
    @GetMapping("/order-items/order/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItem> orderItems = orderService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }
    
    // Create order item
    @PostMapping("/order-items")
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItem orderItem) {
        OrderItem createdOrderItem = orderService.createOrderItem(orderItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrderItem);
    }
    
    // Update order item
    @PutMapping("/order-items/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id, @RequestBody OrderItem orderItemDetails) {
        OrderItem updatedOrderItem = orderService.updateOrderItem(id, orderItemDetails);
        if (updatedOrderItem != null) {
            return ResponseEntity.ok(updatedOrderItem);
        }
        return ResponseEntity.notFound().build();
    }
    
    // Delete order item
    @DeleteMapping("/order-items/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        boolean deleted = orderService.deleteOrderItem(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
} 