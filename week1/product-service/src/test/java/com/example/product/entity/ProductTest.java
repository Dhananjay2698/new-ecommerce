package com.example.product.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

class ProductTest {

    private Product product;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        product = new Product();
        testDateTime = LocalDateTime.now();
    }

    @Test
    void testProductCreation() {
        assertNotNull(product);
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getPrice());
        assertNull(product.getStockQuantity());
        assertNotNull(product.getCreatedDate());
        assertNull(product.getUpdatedDate());
    }

    @Test
    void testIdGetterAndSetter() {
        Long id = 1L;
        product.setId(id);
        assertEquals(id, product.getId());
    }

    @Test
    void testNameGetterAndSetter() {
        String name = "Test Product";
        product.setName(name);
        assertEquals(name, product.getName());
    }

    @Test
    void testPriceGetterAndSetter() {
        BigDecimal price = new BigDecimal("99.99");
        product.setPrice(price);
        assertEquals(price, product.getPrice());
    }

    @Test
    void testPriceWithDifferentValues() {
        BigDecimal price1 = new BigDecimal("0.00");
        BigDecimal price2 = new BigDecimal("999.99");
        BigDecimal price3 = new BigDecimal("1234.56");

        product.setPrice(price1);
        assertEquals(price1, product.getPrice());

        product.setPrice(price2);
        assertEquals(price2, product.getPrice());

        product.setPrice(price3);
        assertEquals(price3, product.getPrice());
    }

    @Test
    void testStockQuantityGetterAndSetter() {
        Integer stockQuantity = 100;
        product.setStockQuantity(stockQuantity);
        assertEquals(stockQuantity, product.getStockQuantity());
    }

    @Test
    void testStockQuantityWithDifferentValues() {
        Integer stock1 = 0;
        Integer stock2 = 999;
        Integer stock3 = -5; // Negative stock should be allowed for testing

        product.setStockQuantity(stock1);
        assertEquals(stock1, product.getStockQuantity());

        product.setStockQuantity(stock2);
        assertEquals(stock2, product.getStockQuantity());

        product.setStockQuantity(stock3);
        assertEquals(stock3, product.getStockQuantity());
    }

    @Test
    void testCreatedDateGetterAndSetter() {
        product.setCreatedDate(testDateTime);
        assertEquals(testDateTime, product.getCreatedDate());
    }

    @Test
    void testUpdatedDateGetterAndSetter() {
        product.setUpdatedDate(testDateTime);
        assertEquals(testDateTime, product.getUpdatedDate());
    }

    @Test
    void testProductWithAllFields() {
        Long id = 1L;
        String name = "Premium Product";
        BigDecimal price = new BigDecimal("199.99");
        Integer stockQuantity = 50;
        LocalDateTime createdDate = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedDate = LocalDateTime.now();

        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setCreatedDate(createdDate);
        product.setUpdatedDate(updatedDate);

        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(stockQuantity, product.getStockQuantity());
        assertEquals(createdDate, product.getCreatedDate());
        assertEquals(updatedDate, product.getUpdatedDate());
    }

    @Test
    void testDefaultCreatedDate() {
        Product newProduct = new Product();
        assertNotNull(newProduct.getCreatedDate());
        assertTrue(newProduct.getCreatedDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(newProduct.getCreatedDate().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void testPricePrecision() {
        BigDecimal precisePrice = new BigDecimal("123.456789");
        product.setPrice(precisePrice);
        assertEquals(precisePrice, product.getPrice());
    }

    @Test
    void testZeroPrice() {
        BigDecimal zeroPrice = BigDecimal.ZERO;
        product.setPrice(zeroPrice);
        assertEquals(zeroPrice, product.getPrice());
    }

    @Test
    void testZeroStockQuantity() {
        Integer zeroStock = 0;
        product.setStockQuantity(zeroStock);
        assertEquals(zeroStock, product.getStockQuantity());
    }
} 