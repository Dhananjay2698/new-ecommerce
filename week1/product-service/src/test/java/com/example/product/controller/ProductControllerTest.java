package com.example.product.controller;

import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import com.example.product.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.example.product.config.NoSecurityConfig;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(NoSecurityConfig.class)
@WebMvcTest(
    value = ProductController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfiguration.class)
    }
)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private Product updatedProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStockQuantity(100);
        testProduct.setCreatedDate(LocalDateTime.now());

        updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(new BigDecimal("149.99"));
        updatedProduct.setStockQuantity(75);
    }

    @Test
    void testGetAllProducts() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].price").value(99.99))
                .andExpect(jsonPath("$[0].stockQuantity").value(100));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductById_WhenProductExists() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.stockQuantity").value(100));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void testGetProductById_WhenProductDoesNotExist() throws Exception {
        // Arrange
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    void testCreateProduct() throws Exception {
        // Arrange
        Product productToCreate = new Product();
        productToCreate.setName("New Product");
        productToCreate.setPrice(new BigDecimal("199.99"));
        productToCreate.setStockQuantity(50);

        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.stockQuantity").value(100));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testCreateProduct_WithInvalidData() throws Exception {
        // Arrange
        Product invalidProduct = new Product();
        // Missing required fields

        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isCreated()); // Controller doesn't validate, so it returns 201

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testUpdateProductStock_WhenProductExists() throws Exception {
        // Arrange
        Product stockUpdate = new Product();
        stockUpdate.setStockQuantity(200);

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Test Product");
        updatedProduct.setPrice(new BigDecimal("99.99"));
        updatedProduct.setStockQuantity(200);

        when(productService.updateProductStock(eq(1L), eq(200))).thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(patch("/products/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.stockQuantity").value(200));

        verify(productService, times(1)).updateProductStock(eq(1L), eq(200));
    }

    @Test
    void testUpdateProductStock_WhenProductDoesNotExist() throws Exception {
        // Arrange
        Product stockUpdate = new Product();
        stockUpdate.setStockQuantity(200);

        when(productService.updateProductStock(eq(999L), eq(200))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(patch("/products/999/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockUpdate)))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateProductStock(eq(999L), eq(200));
    }

    @Test
    void testUpdateProductStock_WhenExceptionOccurs() throws Exception {
        // Arrange
        Product stockUpdate = new Product();
        stockUpdate.setStockQuantity(200);

        when(productService.updateProductStock(eq(1L), eq(200)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(patch("/products/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockUpdate)))
                .andExpect(status().isBadRequest());

        verify(productService, times(1)).updateProductStock(eq(1L), eq(200));
    }

    @Test
    void testDeleteProduct_WhenProductExists() throws Exception {
        // Arrange
        when(productService.deleteProduct(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProduct_WhenProductDoesNotExist() throws Exception {
        // Arrange
        when(productService.deleteProduct(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct(999L);
    }

    @Test
    void testDeleteProduct_WhenExceptionOccurs() throws Exception {
        // Arrange
        when(productService.deleteProduct(1L)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isInternalServerError());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProductByName_WhenProductsExist() throws Exception {
        // Arrange
        when(productService.deleteProductByName("Test Product")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/products/name/Test Product"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProductByName("Test Product");
    }

    @Test
    void testDeleteProductByName_WhenNoProductsExist() throws Exception {
        // Arrange
        when(productService.deleteProductByName("Non-existent Product")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/products/name/Non-existent Product"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProductByName("Non-existent Product");
    }

    @Test
    void testDeleteProductByName_WhenExceptionOccurs() throws Exception {
        // Arrange
        when(productService.deleteProductByName("Test Product"))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(delete("/products/name/Test Product"))
                .andExpect(status().isInternalServerError());

        verify(productService, times(1)).deleteProductByName("Test Product");
    }
} 