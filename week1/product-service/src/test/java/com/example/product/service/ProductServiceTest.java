package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

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
    void testGetAllProducts() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.getAllProducts();

        // Assert
        assertNotNull(actualProducts);
        assertEquals(1, actualProducts.size());
        assertEquals(testProduct, actualProducts.get(0));
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById_WhenProductExists() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // Act
        Optional<Product> result = productService.getProductById(productId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testProduct, result.get());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testGetProductById_WhenProductDoesNotExist() {
        // Arrange
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.getProductById(productId);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testCreateProduct() {
        // Arrange
        Product productToCreate = new Product();
        productToCreate.setName("New Product");
        productToCreate.setPrice(new BigDecimal("199.99"));
        productToCreate.setStockQuantity(50);

        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Product createdProduct = productService.createProduct(productToCreate);

        // Assert
        assertNotNull(createdProduct);
        assertEquals(testProduct, createdProduct);
        verify(productRepository, times(1)).save(productToCreate);
    }

    @Test
    void testDeleteProduct_WhenProductExists() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).deleteById(productId);

        // Act
        boolean result = productService.deleteProduct(productId);

        // Assert
        assertTrue(result);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void testDeleteProduct_WhenProductDoesNotExist() {
        // Arrange
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        boolean result = productService.deleteProduct(productId);

        // Assert
        assertFalse(result);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteProduct_WhenExceptionOccurs() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        doThrow(new RuntimeException("Database error")).when(productRepository).deleteById(productId);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> productService.deleteProduct(productId));
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void testDeleteProductByName_WhenProductsExist() {
        // Arrange
        String productName = "Test Product";
        List<Product> productsToDelete = Arrays.asList(testProduct);
        when(productRepository.findByName(productName)).thenReturn(productsToDelete);
        doNothing().when(productRepository).deleteAll(productsToDelete);

        // Act
        boolean result = productService.deleteProductByName(productName);

        // Assert
        assertTrue(result);
        verify(productRepository, times(1)).findByName(productName);
        verify(productRepository, times(1)).deleteAll(productsToDelete);
    }

    @Test
    void testDeleteProductByName_WhenNoProductsExist() {
        // Arrange
        String productName = "Non-existent Product";
        when(productRepository.findByName(productName)).thenReturn(Arrays.asList());

        // Act
        boolean result = productService.deleteProductByName(productName);

        // Assert
        assertFalse(result);
        verify(productRepository, times(1)).findByName(productName);
        verify(productRepository, never()).deleteAll(any());
    }

    @Test
    void testUpdateProductStock_WhenProductExists() {
        // Arrange
        Long productId = 1L;
        Integer newStockQuantity = 200;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setStockQuantity(newStockQuantity);
            return savedProduct;
        });

        // Act
        Product updatedProduct = productService.updateProductStock(productId, newStockQuantity);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals(newStockQuantity, updatedProduct.getStockQuantity());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProductStock_WhenProductDoesNotExist() {
        // Arrange
        Long productId = 999L;
        Integer newStockQuantity = 200;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        Product result = productService.updateProductStock(productId, newStockQuantity);

        // Assert
        assertNull(result);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProductStock_WithZeroStock() {
        // Arrange
        Long productId = 1L;
        Integer newStockQuantity = 0;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setStockQuantity(newStockQuantity);
            return savedProduct;
        });

        // Act
        Product updatedProduct = productService.updateProductStock(productId, newStockQuantity);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals(newStockQuantity, updatedProduct.getStockQuantity());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProductStock_WithNegativeStock() {
        // Arrange
        Long productId = 1L;
        Integer newStockQuantity = -10;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setStockQuantity(newStockQuantity);
            return savedProduct;
        });

        // Act
        Product updatedProduct = productService.updateProductStock(productId, newStockQuantity);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals(newStockQuantity, updatedProduct.getStockQuantity());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }
} 