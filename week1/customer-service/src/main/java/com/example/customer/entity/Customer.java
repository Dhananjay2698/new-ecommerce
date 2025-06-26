package com.example.customer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // Getters and setters
    public Long getId() { 
        return id; 
    }
    public void setId(Long id) {
        this.id = id; 
    }
    public String getName() { 
        return name; 
    }
    public void setName(String name) { 
        this.name = name; 
    }
    public String getEmail() { 
        return email; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }
    public LocalDateTime getCreatedDate() { 
        return createdDate; 
    }
    public void setCreatedDate(LocalDateTime createdDate) { 
        this.createdDate = createdDate; 
    }
    public LocalDateTime getUpdatedDate() { 
        return updatedDate; 
    }
    public void setUpdatedDate(LocalDateTime updatedDate) { 
        this.updatedDate = updatedDate; 
    }
} 