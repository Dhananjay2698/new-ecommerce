package com.example.product.controller;

import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    // Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // Create new product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
    
    // Update product stock
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateProductStock(
            @PathVariable("id") Long id, 
            @RequestBody Product stockUpdate) {
        try {
            System.out.println("Updating stock for product ID: " + id + " to quantity: " + stockUpdate.getStockQuantity());
            Product updatedProduct = productService.updateProductStock(id, stockUpdate.getStockQuantity());
            if (updatedProduct != null) {
                System.out.println("Stock updated successfully for product: " + updatedProduct.getName());
                return ResponseEntity.ok(updatedProduct);
            } else {
                System.out.println("Product not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error updating stock for product ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Delete product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        try {
            System.out.println("Attempting to delete product with ID: " + id);
            boolean deleted = productService.deleteProduct(id);
            if (deleted) {
                System.out.println("Product deleted successfully with ID: " + id);
                return ResponseEntity.noContent().build();
            } else {
                System.out.println("Product not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error deleting product with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Delete product by name
    @DeleteMapping("/name/{name}")
    public ResponseEntity<Void> deleteProductByName(@PathVariable("name") String name) {
        try {
            System.out.println("Attempting to delete product with name: " + name);
            boolean deleted = productService.deleteProductByName(name);
            if (deleted) {
                System.out.println("Product deleted successfully with name: " + name);
                return ResponseEntity.noContent().build();
            } else {
                System.out.println("Product not found with name: " + name);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error deleting product with name " + name + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
} 