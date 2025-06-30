package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.OrderItemRepository;
import com.example.order.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private ProductServiceClient productServiceClient;
    
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
        Order savedOrder = orderRepository.save(order);
        
        // Send order confirmation email
        try {
            List<OrderItem> orderItems = getOrderItemsByOrderId(savedOrder.getId());
            // Note: In a real application, you would get customer email from customer service
            String customerEmail = "customer@example.com"; // This should come from customer service
            emailService.sendOrderConfirmationEmail(savedOrder, orderItems, customerEmail);
        } catch (Exception e) {
            // Log error but don't fail the order creation
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
        }
        
        return savedOrder;
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
    
    public Optional<Order> updateOrderStatus(Long id, String status) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(status);
            order.setUpdatedDate(java.time.LocalDateTime.now());
            orderRepository.save(order);
            
            // Send order status update email
            try {
                // Note: In a real application, you would get customer email from customer service
                String customerEmail = "customer@example.com"; // This should come from customer service
                emailService.sendOrderStatusUpdateEmail(order, customerEmail, status);
            } catch (Exception e) {
                // Log error but don't fail the status update
                System.err.println("Failed to send order status update email: " + e.getMessage());
            }
            
            return Optional.of(order);
        }
        return Optional.empty();
    }
    
    // ========== BUSINESS LOGIC METHODS ==========
    
    // Get sum of total amount for a given order
    public BigDecimal getOrderTotalAmount(Long orderId) {
        List<OrderItem> orderItems = getOrderItemsByOrderId(orderId);
        if (orderItems.isEmpty()) {
            return null; // Order not found or no items
        }
        
        // Calculate total using actual product prices from product service
        BigDecimal totalAmount = orderItems.stream()
                .map(item -> {
                    BigDecimal productPrice = productServiceClient.getProductPrice(item.getProductId());
                    return productPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalAmount;
    }
    
    // Count Number of Orders from a particular customer
    public Long getOrderCountByCustomer(Long customerId) {
        return orderRepository.countByCustomerId(customerId);
    }
    
    // Count Distinct orders in which a given Product Appears
    public Long getDistinctOrderCountByProduct(Long productId) {
        List<OrderItem> orderItems = orderItemRepository.findByProductId(productId);
        return orderItems.stream()
                .map(OrderItem::getOrderId)
                .distinct()
                .count();
    }
    
    // Count total purchases made by customer till date or for a given date range
    public Long getCustomerPurchaseCount(Long customerId, String startDate, String endDate) {
        List<Order> customerOrders = getOrdersByCustomerId(customerId);
        
        if (startDate == null && endDate == null) {
            // Count all orders for the customer
            return (long) customerOrders.size();
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = startDate != null ? LocalDate.parse(startDate, formatter) : LocalDate.MIN;
        LocalDate end = endDate != null ? LocalDate.parse(endDate, formatter) : LocalDate.MAX;
        
        return customerOrders.stream()
                .filter(order -> {
                    LocalDate orderDate = order.getOrderedDate().toLocalDate();
                    return !orderDate.isBefore(start) && !orderDate.isAfter(end);
                })
                .count();
    }
} 