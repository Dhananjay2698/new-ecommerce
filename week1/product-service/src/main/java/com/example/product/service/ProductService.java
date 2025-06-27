package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // Get product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    // Create new product
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    // Delete product
    public boolean deleteProduct(Long id) {
        try {
            System.out.println("Service: Attempting to delete product with ID: " + id);
            Optional<Product> optionalProduct = productRepository.findById(id);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                System.out.println("Service: Found product: " + product.getName() + ", proceeding with deletion");
                productRepository.deleteById(id);
                System.out.println("Service: Product deleted successfully from repository");
                return true;
            } else {
                System.out.println("Service: Product not found with ID: " + id);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Service: Error deleting product with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to let controller handle it
        }
    }
    
    // Delete product by name
    public boolean deleteProductByName(String name) {
        List<Product> products = productRepository.findByName(name);
        if (!products.isEmpty()) {
            productRepository.deleteAll(products);
            return true;
        }
        return false;
    }
    
    // Update product stock
    public Product updateProductStock(Long id, Integer newStockQuantity) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setStockQuantity(newStockQuantity);
            return productRepository.save(product);
        }
        return null;
    }
} 