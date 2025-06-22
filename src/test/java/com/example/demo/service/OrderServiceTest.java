package com.example.demo.service;

import com.example.demo.ModelUtils;
import com.example.demo.dto.UserOrderDTO;
import com.example.demo.model.Inventory;
import com.example.demo.model.Merchant;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.MerchantRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private MerchantRepository merchantRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_ShouldCompleteSuccessfully_WithValidInput() {
        // Arrange
        UserOrderDTO dto = new UserOrderDTO(1L, 100L, 2);

        User user = new User();
        user.setId(1L);
        user.setBalance(new BigDecimal("500.00"));

        Product product = ModelUtils.mockProduct();
        product.setPrice(new BigDecimal("100.00"));

        Merchant merchant = new Merchant();
        merchant.setId(1L);

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setMerchant(merchant);
        inventory.setQuantity(10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(inventoryRepository.findByProductId(100L)).thenReturn(Optional.of(inventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        orderService.createOrder(dto);

        // Assert
        // Verify user balance was deducted
        assertEquals(new BigDecimal("300.00"), user.getBalance());

        // Verify merchant earnings
        assertEquals(new BigDecimal("200.00"), merchant.getBalance());

        // Verify inventory quantity was reduced
        assertEquals(8, inventory.getQuantity());

        // Verify repositories were called
        verify(userRepository).save(user);
        verify(merchantRepository).save(merchant);
        verify(inventoryRepository).save(inventory);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenQuantityZeroOrNegative() {
        // Arrange
        UserOrderDTO zeroDto = new UserOrderDTO(1L, 100L, 0);
        UserOrderDTO negativeDto = new UserOrderDTO(1L, 100L, -1);

        // Act & Assert
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> orderService.createOrder(zeroDto),
                        "Quantity must be greater than 0"),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> orderService.createOrder(negativeDto),
                        "Quantity must be greater than 0")
        );

        verifyNoInteractions(userRepository, merchantRepository, inventoryRepository, orderRepository);
    }

    @Test
    void createOrder_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        UserOrderDTO dto = new UserOrderDTO(1L, 100L, 2);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderService.createOrder(dto));

        assertEquals("User not found", exception.getMessage());
        verifyNoInteractions(merchantRepository, inventoryRepository, orderRepository);
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductNotInStock() {
        // Arrange
        UserOrderDTO dto = new UserOrderDTO(1L, 100L, 2);
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(inventoryRepository.findByProductId(100L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderService.createOrder(dto));

        assertEquals("Product not in stock", exception.getMessage());
        verifyNoInteractions(merchantRepository, orderRepository);
    }

    @Test
    void createOrder_ShouldThrowException_WhenInsufficientUserBalance() {
        // Arrange
        UserOrderDTO dto = new UserOrderDTO(1L, 100L, 3);

        User user = new User();
        user.setId(1L);
        user.setBalance(new BigDecimal("100.00"));

        Product product = ModelUtils.mockProduct();
        product.setPrice(new BigDecimal("100.00"));

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(inventoryRepository.findByProductId(100L)).thenReturn(Optional.of(inventory));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(dto));

        assertEquals("Insufficient balance", exception.getMessage());
        verifyNoInteractions(merchantRepository, orderRepository);
    }

    @Test
    void createOrder_ShouldThrowException_WhenInsufficientInventory() {
        // Arrange
        UserOrderDTO dto = new UserOrderDTO(1L, 100L, 5);

        User user = new User();
        user.setId(1L);
        user.setBalance(new BigDecimal("1000.00"));

        Product product = ModelUtils.mockProduct();
        product.setPrice(new BigDecimal("100.00"));

        Inventory inventory = new Inventory();
        inventory.setMerchant(ModelUtils.mockMerchant());
        inventory.setProduct(product);
        inventory.setQuantity(3);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(inventoryRepository.findByProductId(100L)).thenReturn(Optional.of(inventory));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(dto));

        assertEquals("Insufficient stock", exception.getMessage());
        verifyNoInteractions(merchantRepository, orderRepository);
    }

    @Test
    void createOrder_ShouldHandlePrecisionCorrectly() {
        // Arrange
        UserOrderDTO dto = new UserOrderDTO(1L, 100L, 3);

        User user = new User();
        user.setId(1L);
        user.setBalance(new BigDecimal("500.000000"));

        Product product = ModelUtils.mockProduct();
        product.setPrice(new BigDecimal("99.999999"));
        Merchant merchant = new Merchant();
        merchant.setId(1L);

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setMerchant(merchant);
        inventory.setQuantity(10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(inventoryRepository.findByProductId(100L)).thenReturn(Optional.of(inventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        orderService.createOrder(dto);

        // Assert
        assertEquals(new BigDecimal("200.000003"), user.getBalance()); // 500 - (99.999999 * 3)
        assertEquals(new BigDecimal("299.999997"), merchant.getBalance());
        assertEquals(7, inventory.getQuantity());
    }
}