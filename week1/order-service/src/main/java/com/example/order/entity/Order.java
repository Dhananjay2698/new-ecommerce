package com.example.order.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String status;

    @Column(name = "ordered_date", nullable = false, updatable = false)
    private LocalDateTime orderedDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // Getters and setters
    public Long getId() { 
        return id;
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public Long getCustomerId() { 
        return customerId; 
    }
    public void setCustomerId(Long customerId) { 
        this.customerId = customerId; 
    }
    public String getStatus() { 
        return status; 
    }
    public void setStatus(String status) { 
        this.status = status;
    }
    public LocalDateTime getOrderedDate() { 
        return orderedDate; 
    }
    public void setOrderedDate(LocalDateTime orderedDate) { 
        this.orderedDate = orderedDate; 
    }
    public LocalDateTime getUpdatedDate() { 
        return updatedDate; 
    }
    public void setUpdatedDate(LocalDateTime updatedDate) { 
        this.updatedDate = updatedDate; 
    }
} 