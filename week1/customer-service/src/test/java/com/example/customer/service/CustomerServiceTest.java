package com.example.customer.service;

import com.example.customer.entity.Customer;
import com.example.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private Customer updatedCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setCreatedDate(LocalDateTime.now());

        updatedCustomer = new Customer();
        updatedCustomer.setName("Jane Smith");
        updatedCustomer.setEmail("jane.smith@example.com");
    }

    @Test
    void testGetAllCustomers() {
        // Arrange
        List<Customer> expectedCustomers = Arrays.asList(testCustomer);
        when(customerRepository.findAll()).thenReturn(expectedCustomers);

        // Act
        List<Customer> actualCustomers = customerService.getAllCustomers();

        // Assert
        assertNotNull(actualCustomers);
        assertEquals(1, actualCustomers.size());
        assertEquals(testCustomer, actualCustomers.get(0));
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetCustomerById_WhenCustomerExists() {
        // Arrange
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));

        // Act
        Optional<Customer> result = customerService.getCustomerById(customerId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCustomer, result.get());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void testGetCustomerById_WhenCustomerDoesNotExist() {
        // Arrange
        Long customerId = 999L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = customerService.getCustomerById(customerId);

        // Assert
        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void testSaveCustomer() {
        // Arrange
        Customer customerToSave = new Customer();
        customerToSave.setName("New Customer");
        customerToSave.setEmail("new@example.com");
        
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        Customer savedCustomer = customerService.saveCustomer(customerToSave);

        // Assert
        assertNotNull(savedCustomer);
        assertEquals(testCustomer, savedCustomer);
        verify(customerRepository, times(1)).save(customerToSave);
    }

    @Test
    void testUpdateCustomer_WhenCustomerExists() {
        // Arrange
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        Optional<Customer> result = customerService.updateCustomer(customerId, updatedCustomer);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCustomer, result.get());
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_WhenCustomerDoesNotExist() {
        // Arrange
        Long customerId = 999L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = customerService.updateCustomer(customerId, updatedCustomer);

        // Assert
        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_UpdatesNameAndEmail() {
        // Arrange
        Long customerId = 1L;
        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setName("Old Name");
        existingCustomer.setEmail("old@example.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer savedCustomer = invocation.getArgument(0);
            savedCustomer.setId(customerId);
            return savedCustomer;
        });

        // Act
        Optional<Customer> result = customerService.updateCustomer(customerId, updatedCustomer);

        // Assert
        assertTrue(result.isPresent());
        Customer updatedResult = result.get();
        assertEquals(updatedCustomer.getName(), updatedResult.getName());
        assertEquals(updatedCustomer.getEmail(), updatedResult.getEmail());
        assertEquals(customerId, updatedResult.getId());
    }
} 