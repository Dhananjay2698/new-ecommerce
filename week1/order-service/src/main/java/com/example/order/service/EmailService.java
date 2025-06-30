package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    public void sendOrderConfirmationEmail(Order order, List<OrderItem> orderItems, String customerEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(customerEmail);
            helper.setSubject("Order Confirmation - Order #" + order.getId());
            helper.setFrom("noreply@ecommerce.com");
            
            // Create context for Thymeleaf template
            Context context = new Context();
            context.setVariable("order", order);
            context.setVariable("orderItems", orderItems);
            context.setVariable("orderNumber", order.getId());
            context.setVariable("orderDate", order.getOrderedDate());
            context.setVariable("orderStatus", order.getStatus());
            
            // Process HTML template
            String htmlContent = templateEngine.process("order-confirmation", context);
            helper.setText(htmlContent, true);
            
            // Send email
            mailSender.send(message);
            logger.info("Order confirmation email sent successfully to {} for order #{}", customerEmail, order.getId());
            
        } catch (MessagingException e) {
            logger.error("Failed to send order confirmation email for order #{}: {}", order.getId(), e.getMessage(), e);
        }
    }
    
    public void sendOrderStatusUpdateEmail(Order order, String customerEmail, String newStatus) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(customerEmail);
            helper.setSubject("Order Status Update - Order #" + order.getId());
            helper.setFrom("noreply@ecommerce.com");
            
            // Create context for Thymeleaf template
            Context context = new Context();
            context.setVariable("order", order);
            context.setVariable("orderNumber", order.getId());
            context.setVariable("newStatus", newStatus);
            context.setVariable("updateDate", order.getUpdatedDate());
            
            // Process HTML template
            String htmlContent = templateEngine.process("order-status-update", context);
            helper.setText(htmlContent, true);
            
            // Send email
            mailSender.send(message);
            logger.info("Order status update email sent successfully to {} for order #{}", customerEmail, order.getId());
            
        } catch (MessagingException e) {
            logger.error("Failed to send order status update email for order #{}: {}", order.getId(), e.getMessage(), e);
        }
    }
} 