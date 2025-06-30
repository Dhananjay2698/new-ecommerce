package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private Order testOrder;
    private List<OrderItem> testOrderItems;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerId(1L);
        testOrder.setStatus("PENDING");
        testOrder.setOrderedDate(LocalDateTime.now());

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrderId(1L);
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);

        testOrderItems = Arrays.asList(orderItem);
    }

    @Test
    void testSendOrderConfirmationEmail_Success() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any())).thenReturn("<html>Test email</html>");

        // Act
        emailService.sendOrderConfirmationEmail(testOrder, testOrderItems, "test@example.com");

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(templateEngine, times(1)).process(eq("order-confirmation"), any());
    }

    @Test
    void testSendOrderStatusUpdateEmail_Success() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any())).thenReturn("<html>Test email</html>");

        // Act
        emailService.sendOrderStatusUpdateEmail(testOrder, "test@example.com", "COMPLETED");

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(templateEngine, times(1)).process(eq("order-status-update"), any());
    }

    @Test
    void testSendOrderConfirmationEmail_Exception() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            emailService.sendOrderConfirmationEmail(testOrder, testOrderItems, "test@example.com");
        });
    }

    @Test
    void testSendOrderStatusUpdateEmail_Exception() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            emailService.sendOrderStatusUpdateEmail(testOrder, "test@example.com", "COMPLETED");
        });
    }
}
