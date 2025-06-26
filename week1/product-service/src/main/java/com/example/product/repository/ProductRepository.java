package com.example.product.repository;

import com.example.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find all products using HQL
    @Query("SELECT p FROM Product p")
    List<Product> findAll();
    
    // Find product by ID using HQL
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findById(@Param("id") Long id);
    
    // Find products by name containing (case insensitive) using HQL
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Find products by price range using HQL
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    // Find products with stock quantity greater than using HQL
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > :stockQuantity")
    List<Product> findByStockQuantityGreaterThan(@Param("stockQuantity") Integer stockQuantity);
    
    // Find products with low stock (less than or equal to threshold) using HQL
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold")
    List<Product> findByStockQuantityLessThanEqual(@Param("threshold") Integer threshold);
    
    // Find all products ordered by price ascending using HQL
    @Query("SELECT p FROM Product p ORDER BY p.price ASC")
    List<Product> findAllByOrderByPriceAsc();
    
    // Find all products ordered by price descending using HQL
    @Query("SELECT p FROM Product p ORDER BY p.price DESC")
    List<Product> findAllByOrderByPriceDesc();
    
    // Find products by exact name using HQL
    @Query("SELECT p FROM Product p WHERE p.name = :name")
    List<Product> findByName(@Param("name") String name);
    
    // Find products with zero stock using HQL
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0")
    List<Product> findOutOfStockProducts();
    
    // Find products with stock quantity between range using HQL
    @Query("SELECT p FROM Product p WHERE p.stockQuantity BETWEEN :minStock AND :maxStock")
    List<Product> findByStockQuantityBetween(@Param("minStock") Integer minStock, @Param("maxStock") Integer maxStock);
    
    // Find products by price greater than using HQL
    @Query("SELECT p FROM Product p WHERE p.price > :price")
    List<Product> findByPriceGreaterThan(@Param("price") BigDecimal price);
    
    // Find products by price less than using HQL
    @Query("SELECT p FROM Product p WHERE p.price < :price")
    List<Product> findByPriceLessThan(@Param("price") BigDecimal price);
    
    // Find products ordered by stock quantity ascending using HQL
    @Query("SELECT p FROM Product p ORDER BY p.stockQuantity ASC")
    List<Product> findAllByOrderByStockQuantityAsc();
    
    // Find products ordered by stock quantity descending using HQL
    @Query("SELECT p FROM Product p ORDER BY p.stockQuantity DESC")
    List<Product> findAllByOrderByStockQuantityDesc();
    
    // Find products ordered by creation date descending (newest first) using HQL
    @Query("SELECT p FROM Product p ORDER BY p.createdDate DESC")
    List<Product> findAllByOrderByCreatedDateDesc();
    
    // Find products ordered by creation date ascending (oldest first) using HQL
    @Query("SELECT p FROM Product p ORDER BY p.createdDate ASC")
    List<Product> findAllByOrderByCreatedDateAsc();
    
    // Find products by name containing and price range using HQL
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByNameContainingAndPriceBetween(@Param("name") String name, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    // Find products by name containing and stock greater than using HQL
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.stockQuantity > :stockQuantity")
    List<Product> findByNameContainingAndStockQuantityGreaterThan(@Param("name") String name, @Param("stockQuantity") Integer stockQuantity);
    
    // Count products by stock quantity greater than using HQL
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity > :stockQuantity")
    Long countByStockQuantityGreaterThan(@Param("stockQuantity") Integer stockQuantity);
    
    // Count products by price range using HQL
    @Query("SELECT COUNT(p) FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Long countByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    // Find products with highest price using HQL
    @Query("SELECT p FROM Product p WHERE p.price = (SELECT MAX(p2.price) FROM Product p2)")
    List<Product> findProductsWithHighestPrice();
    
    // Find products with lowest price using HQL
    @Query("SELECT p FROM Product p WHERE p.price = (SELECT MIN(p2.price) FROM Product p2)")
    List<Product> findProductsWithLowestPrice();
    
    // Find products with highest stock using HQL
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = (SELECT MAX(p2.stockQuantity) FROM Product p2)")
    List<Product> findProductsWithHighestStock();
    
    // Find products with lowest stock using HQL
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = (SELECT MIN(p2.stockQuantity) FROM Product p2)")
    List<Product> findProductsWithLowestStock();
    
    // Delete product by ID using HQL
    @Query("DELETE FROM Product p WHERE p.id = :id")
    void deleteById(@Param("id") Long id);
} 