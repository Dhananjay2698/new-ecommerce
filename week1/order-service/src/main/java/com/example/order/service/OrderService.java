package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    // Get order by ID
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    // Create new order
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
    
    // Update order
    public Order updateOrder(Long id, Order orderDetails) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setCustomerId(orderDetails.getCustomerId());
            order.setStatus(orderDetails.getStatus());
            order.setOrderedDate(orderDetails.getOrderedDate());
            order.setUpdatedDate(orderDetails.getUpdatedDate());
            return orderRepository.save(order);
        }
        return null;
    }
    
    // Delete order
    public boolean deleteOrder(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Get orders by customer ID
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    // Get orders by status
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
    
    // Get all order items
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }
    
    // Get order items by order ID
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
    
    // Create order item
    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }
    
    // Update order item
    public OrderItem updateOrderItem(Long id, OrderItem orderItemDetails) {
        Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(id);
        if (optionalOrderItem.isPresent()) {
            OrderItem orderItem = optionalOrderItem.get();
            orderItem.setOrderId(orderItemDetails.getOrderId());
            orderItem.setProductId(orderItemDetails.getProductId());
            orderItem.setQuantity(orderItemDetails.getQuantity());
            return orderItemRepository.save(orderItem);
        }
        return null;
    }
    
    // Delete order item
    public boolean deleteOrderItem(Long id) {
        Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(id);
        if (optionalOrderItem.isPresent()) {
            orderItemRepository.deleteById(id);
            return true;
        }
        return false;
    }
} 