package com.example.customer.controller;

import com.example.customer.entity.Customer;
import com.example.customer.service.CustomerService;
import com.example.customer.service.JwtService;
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
import com.example.customer.config.NoSecurityConfig;
import org.springframework.context.annotation.Import;

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
    value = CustomerController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfiguration.class)
    }
)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testGetAllCustomers() throws Exception {
        // Arrange
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerService.getAllCustomers()).thenReturn(customers);

        // Act & Assert
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void testGetCustomerById_WhenCustomerExists() throws Exception {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(testCustomer));

        // Act & Assert
        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(customerService, times(1)).getCustomerById(1L);
    }

    @Test
    void testGetCustomerById_WhenCustomerDoesNotExist() throws Exception {
        // Arrange
        when(customerService.getCustomerById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/customers/999"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerById(999L);
    }

    @Test
    void testGetCustomerById_WhenExceptionOccurs() throws Exception {
        // Arrange
        when(customerService.getCustomerById(1L)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isInternalServerError());

        verify(customerService, times(1)).getCustomerById(1L);
    }

    @Test
    void testCreateCustomer() throws Exception {
        // Arrange
        Customer customerToCreate = new Customer();
        customerToCreate.setName("New Customer");
        customerToCreate.setEmail("new@example.com");

        when(customerService.saveCustomer(any(Customer.class))).thenReturn(testCustomer);

        // Act & Assert
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(customerService, times(1)).saveCustomer(any(Customer.class));
    }

    @Test
    void testCreateCustomer_WithInvalidData() throws Exception {
        // Arrange
        Customer invalidCustomer = new Customer();
        // Missing required fields

        when(customerService.saveCustomer(any(Customer.class))).thenReturn(testCustomer);

        // Act & Assert
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCustomer)))
                .andExpect(status().isOk()); // Controller doesn't validate, so it returns 200

        verify(customerService, times(1)).saveCustomer(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_WhenCustomerExists() throws Exception {
        // Arrange
        when(customerService.updateCustomer(eq(1L), any(Customer.class)))
                .thenReturn(Optional.of(testCustomer));

        // Act & Assert
        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(customerService, times(1)).updateCustomer(eq(1L), any(Customer.class));
    }

    @Test
    void testUpdateCustomer_WhenCustomerDoesNotExist() throws Exception {
        // Arrange
        when(customerService.updateCustomer(eq(999L), any(Customer.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/customers/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).updateCustomer(eq(999L), any(Customer.class));
    }

    @Test
    void testUpdateCustomer_WithInvalidData() throws Exception {
        // Arrange
        Customer invalidCustomer = new Customer();
        // Missing required fields

        when(customerService.updateCustomer(eq(1L), any(Customer.class)))
                .thenReturn(Optional.of(testCustomer));

        // Act & Assert
        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCustomer)))
                .andExpect(status().isOk()); // Controller doesn't validate, so it returns 200

        verify(customerService, times(1)).updateCustomer(eq(1L), any(Customer.class));
    }

    @Test
    void testDeleteCustomer_WhenCustomerExists() throws Exception {
        // Arrange
        when(customerService.deleteCustomer(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCustomer_WhenCustomerDoesNotExist() throws Exception {
        // Arrange
        when(customerService.deleteCustomer(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/customers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCustomer_WhenExceptionOccurs() throws Exception {
        // Arrange
        when(customerService.deleteCustomer(1L))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isInternalServerError());
    }
} 