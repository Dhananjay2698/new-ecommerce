package com.example.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

@Service
public class ProductServiceClient {
    
    private final RestTemplate restTemplate;
    private final String PRODUCT_SERVICE_URL = "http://localhost:8080/products";
    
    @Autowired
    public ProductServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public BigDecimal getProductPrice(Long productId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                PRODUCT_SERVICE_URL + "/" + productId, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> product = response.getBody();
                if (product.containsKey("price")) {
                    // Handle different price formats (Number, String, etc.)
                    Object priceObj = product.get("price");
                    if (priceObj instanceof Number) {
                        return new BigDecimal(priceObj.toString());
                    } else if (priceObj instanceof String) {
                        return new BigDecimal((String) priceObj);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching product price for ID " + productId + ": " + e.getMessage());
        }
        
        // Return default price if product not found or error occurs
        return new BigDecimal("10.00");
    }
} 