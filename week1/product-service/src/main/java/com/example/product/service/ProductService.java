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
    
    // Update product
    public Product updateProduct(Long id, Product productDetails) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(productDetails.getName());
            product.setPrice(productDetails.getPrice());
            product.setStockQuantity(productDetails.getStockQuantity());
            return productRepository.save(product);
        }
        return null;
    }
    
    // Delete product
    public boolean deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Get products by name (contains)
    public List<Product> getProductsByNameContaining(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    // Get products by exact name
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }
    
    // Get products by price range
    public List<Product> getProductsByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    // Get products with price greater than
    public List<Product> getProductsByPriceGreaterThan(BigDecimal price) {
        return productRepository.findByPriceGreaterThan(price);
    }
    
    // Get products with price less than
    public List<Product> getProductsByPriceLessThan(BigDecimal price) {
        return productRepository.findByPriceLessThan(price);
    }
    
    // Get products with stock greater than
    public List<Product> getProductsByStockQuantityGreaterThan(Integer stockQuantity) {
        return productRepository.findByStockQuantityGreaterThan(stockQuantity);
    }
    
    // Get products with low stock (less than or equal to threshold)
    public List<Product> getProductsWithLowStock(Integer threshold) {
        return productRepository.findByStockQuantityLessThanEqual(threshold);
    }
    
    // Get out of stock products
    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts();
    }
    
    // Get products with stock between range
    public List<Product> getProductsByStockQuantityBetween(Integer minStock, Integer maxStock) {
        return productRepository.findByStockQuantityBetween(minStock, maxStock);
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
    
    // Get products sorted by price (ascending)
    public List<Product> getProductsOrderByPriceAsc() {
        return productRepository.findAllByOrderByPriceAsc();
    }
    
    // Get products sorted by price (descending)
    public List<Product> getProductsOrderByPriceDesc() {
        return productRepository.findAllByOrderByPriceDesc();
    }
    
    // Get products sorted by stock quantity (ascending)
    public List<Product> getProductsOrderByStockQuantityAsc() {
        return productRepository.findAllByOrderByStockQuantityAsc();
    }
    
    // Get products sorted by stock quantity (descending)
    public List<Product> getProductsOrderByStockQuantityDesc() {
        return productRepository.findAllByOrderByStockQuantityDesc();
    }
    
    // Get products sorted by creation date (newest first)
    public List<Product> getProductsOrderByCreatedDateDesc() {
        return productRepository.findAllByOrderByCreatedDateDesc();
    }
    
    // Get products sorted by creation date (oldest first)
    public List<Product> getProductsOrderByCreatedDateAsc() {
        return productRepository.findAllByOrderByCreatedDateAsc();
    }
    
    // Get products by name containing and price range
    public List<Product> getProductsByNameContainingAndPriceBetween(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByNameContainingAndPriceBetween(name, minPrice, maxPrice);
    }
    
    // Get products by name containing and stock greater than
    public List<Product> getProductsByNameContainingAndStockQuantityGreaterThan(String name, Integer stockQuantity) {
        return productRepository.findByNameContainingAndStockQuantityGreaterThan(name, stockQuantity);
    }
    
    // Count products by stock quantity greater than
    public Long countProductsByStockQuantityGreaterThan(Integer stockQuantity) {
        return productRepository.countByStockQuantityGreaterThan(stockQuantity);
    }
    
    // Count products by price range
    public Long countProductsByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.countByPriceBetween(minPrice, maxPrice);
    }
    
    // Get products with highest price
    public List<Product> getProductsWithHighestPrice() {
        return productRepository.findProductsWithHighestPrice();
    }
    
    // Get products with lowest price
    public List<Product> getProductsWithLowestPrice() {
        return productRepository.findProductsWithLowestPrice();
    }
    
    // Get products with highest stock
    public List<Product> getProductsWithHighestStock() {
        return productRepository.findProductsWithHighestStock();
    }
    
    // Get products with lowest stock
    public List<Product> getProductsWithLowestStock() {
        return productRepository.findProductsWithLowestStock();
    }
} 