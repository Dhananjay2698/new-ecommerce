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
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    // Get all products
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    // Get product by ID
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // Create new product
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
    
    // Update product
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        }
        return ResponseEntity.notFound().build();
    }
    
    // Delete product
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // Get products by name (contains)
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> getProductsByNameContaining(@RequestParam String name) {
        List<Product> products = productService.getProductsByNameContaining(name);
        return ResponseEntity.ok(products);
    }
    
    // Get products by exact name
    @GetMapping("/products/exact-name/{name}")
    public ResponseEntity<List<Product>> getProductsByName(@PathVariable String name) {
        List<Product> products = productService.getProductsByName(name);
        return ResponseEntity.ok(products);
    }
    
    // Get products by price range
    @GetMapping("/products/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceBetween(
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        List<Product> products = productService.getProductsByPriceBetween(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }
    
    // Get products with price greater than
    @GetMapping("/products/price-greater-than/{price}")
    public ResponseEntity<List<Product>> getProductsByPriceGreaterThan(@PathVariable BigDecimal price) {
        List<Product> products = productService.getProductsByPriceGreaterThan(price);
        return ResponseEntity.ok(products);
    }
    
    // Get products with price less than
    @GetMapping("/products/price-less-than/{price}")
    public ResponseEntity<List<Product>> getProductsByPriceLessThan(@PathVariable BigDecimal price) {
        List<Product> products = productService.getProductsByPriceLessThan(price);
        return ResponseEntity.ok(products);
    }
    
    // Get products with stock quantity greater than
    @GetMapping("/products/stock-greater-than/{stockQuantity}")
    public ResponseEntity<List<Product>> getProductsByStockQuantityGreaterThan(@PathVariable Integer stockQuantity) {
        List<Product> products = productService.getProductsByStockQuantityGreaterThan(stockQuantity);
        return ResponseEntity.ok(products);
    }
    
    // Get products with low stock
    @GetMapping("/products/low-stock/{threshold}")
    public ResponseEntity<List<Product>> getProductsWithLowStock(@PathVariable Integer threshold) {
        List<Product> products = productService.getProductsWithLowStock(threshold);
        return ResponseEntity.ok(products);
    }
    
    // Get out of stock products
    @GetMapping("/products/out-of-stock")
    public ResponseEntity<List<Product>> getOutOfStockProducts() {
        List<Product> products = productService.getOutOfStockProducts();
        return ResponseEntity.ok(products);
    }
    
    // Get products with stock between range
    @GetMapping("/products/stock-between")
    public ResponseEntity<List<Product>> getProductsByStockQuantityBetween(
            @RequestParam Integer minStock, 
            @RequestParam Integer maxStock) {
        List<Product> products = productService.getProductsByStockQuantityBetween(minStock, maxStock);
        return ResponseEntity.ok(products);
    }
    
    // Update product stock
    @PatchMapping("/products/{id}/stock")
    public ResponseEntity<Product> updateProductStock(
            @PathVariable Long id, 
            @RequestParam Integer stockQuantity) {
        Product updatedProduct = productService.updateProductStock(id, stockQuantity);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        }
        return ResponseEntity.notFound().build();
    }
    
    // Get products sorted by price (ascending)
    @GetMapping("/products/sorted/price-asc")
    public ResponseEntity<List<Product>> getProductsOrderByPriceAsc() {
        List<Product> products = productService.getProductsOrderByPriceAsc();
        return ResponseEntity.ok(products);
    }
    
    // Get products sorted by price (descending)
    @GetMapping("/products/sorted/price-desc")
    public ResponseEntity<List<Product>> getProductsOrderByPriceDesc() {
        List<Product> products = productService.getProductsOrderByPriceDesc();
        return ResponseEntity.ok(products);
    }
    
    // Get products sorted by stock quantity (ascending)
    @GetMapping("/products/sorted/stock-asc")
    public ResponseEntity<List<Product>> getProductsOrderByStockQuantityAsc() {
        List<Product> products = productService.getProductsOrderByStockQuantityAsc();
        return ResponseEntity.ok(products);
    }
    
    // Get products sorted by stock quantity (descending)
    @GetMapping("/products/sorted/stock-desc")
    public ResponseEntity<List<Product>> getProductsOrderByStockQuantityDesc() {
        List<Product> products = productService.getProductsOrderByStockQuantityDesc();
        return ResponseEntity.ok(products);
    }
    
    // Get products sorted by creation date (newest first)
    @GetMapping("/products/sorted/created-desc")
    public ResponseEntity<List<Product>> getProductsOrderByCreatedDateDesc() {
        List<Product> products = productService.getProductsOrderByCreatedDateDesc();
        return ResponseEntity.ok(products);
    }
    
    // Get products sorted by creation date (oldest first)
    @GetMapping("/products/sorted/created-asc")
    public ResponseEntity<List<Product>> getProductsOrderByCreatedDateAsc() {
        List<Product> products = productService.getProductsOrderByCreatedDateAsc();
        return ResponseEntity.ok(products);
    }
    
    // Get products by name containing and price range
    @GetMapping("/products/search/name-and-price")
    public ResponseEntity<List<Product>> getProductsByNameContainingAndPriceBetween(
            @RequestParam String name,
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        List<Product> products = productService.getProductsByNameContainingAndPriceBetween(name, minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }
    
    // Get products by name containing and stock greater than
    @GetMapping("/products/search/name-and-stock")
    public ResponseEntity<List<Product>> getProductsByNameContainingAndStockQuantityGreaterThan(
            @RequestParam String name,
            @RequestParam Integer stockQuantity) {
        List<Product> products = productService.getProductsByNameContainingAndStockQuantityGreaterThan(name, stockQuantity);
        return ResponseEntity.ok(products);
    }
    
    // Count products by stock quantity greater than
    @GetMapping("/products/count/stock-greater-than/{stockQuantity}")
    public ResponseEntity<Long> countProductsByStockQuantityGreaterThan(@PathVariable Integer stockQuantity) {
        Long count = productService.countProductsByStockQuantityGreaterThan(stockQuantity);
        return ResponseEntity.ok(count);
    }
    
    // Count products by price range
    @GetMapping("/products/count/price-range")
    public ResponseEntity<Long> countProductsByPriceBetween(
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        Long count = productService.countProductsByPriceBetween(minPrice, maxPrice);
        return ResponseEntity.ok(count);
    }
    
    // Get products with highest price
    @GetMapping("/products/highest-price")
    public ResponseEntity<List<Product>> getProductsWithHighestPrice() {
        List<Product> products = productService.getProductsWithHighestPrice();
        return ResponseEntity.ok(products);
    }
    
    // Get products with lowest price
    @GetMapping("/products/lowest-price")
    public ResponseEntity<List<Product>> getProductsWithLowestPrice() {
        List<Product> products = productService.getProductsWithLowestPrice();
        return ResponseEntity.ok(products);
    }
    
    // Get products with highest stock
    @GetMapping("/products/highest-stock")
    public ResponseEntity<List<Product>> getProductsWithHighestStock() {
        List<Product> products = productService.getProductsWithHighestStock();
        return ResponseEntity.ok(products);
    }
    
    // Get products with lowest stock
    @GetMapping("/products/lowest-stock")
    public ResponseEntity<List<Product>> getProductsWithLowestStock() {
        List<Product> products = productService.getProductsWithLowestStock();
        return ResponseEntity.ok(products);
    }
} 