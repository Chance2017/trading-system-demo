package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void recharge_ShouldInitializeBalance_WhenBalanceIsNull() {
        // Act
        user.recharge(new BigDecimal("100.00"));

        // Assert
        assertEquals(new BigDecimal("100.00"), user.getBalance());
    }

    @Test
    void recharge_ShouldAddToExistingBalance() {
        // Arrange
        user.recharge(new BigDecimal("200.00"));

        // Act
        user.recharge(new BigDecimal("100.50"));

        // Assert
        assertEquals(new BigDecimal("300.50"), user.getBalance());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-100.00"})
    void recharge_ShouldThrowException_WhenAmountInvalid(BigDecimal amount) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.recharge(amount)
        );

        assertEquals("Recharge amount should be greater than 0", exception.getMessage());
    }

    @Test
    void recharge_ShouldHandlePrecisionCorrectly() {
        // Act
        user.recharge(new BigDecimal("100.123456"));

        // Assert
        assertEquals(new BigDecimal("100.123456"), user.getBalance());
    }

    @Test
    void deductBalance_ShouldInitializeBalance_WhenBalanceIsNull() {
        // Arrange
        user.recharge(new BigDecimal("100.00")); // 先充值

        // Act
        user.deductBalance(new BigDecimal("50.00"));

        // Assert
        assertEquals(new BigDecimal("50.00"), user.getBalance());
    }

    @Test
    void deductBalance_ShouldSubtractFromBalance() {
        // Arrange
        user.recharge(new BigDecimal("300.00"));

        // Act
        user.deductBalance(new BigDecimal("150.50"));

        // Assert
        assertEquals(new BigDecimal("149.50"), user.getBalance());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-50.00"})
    void deductBalance_ShouldThrowException_WhenAmountInvalid(BigDecimal amount) {
        // Arrange
        user.recharge(new BigDecimal("100.00"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.deductBalance(amount)
        );

        assertEquals("Consume amount should be greater than 0", exception.getMessage());
    }

    @Test
    void deductBalance_ShouldThrowException_WhenInsufficientBalance() {
        // Arrange
        user.recharge(new BigDecimal("100.00"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> user.deductBalance(new BigDecimal("150.00"))
        );

        assertEquals("Insufficient balance", exception.getMessage());
        assertEquals(new BigDecimal("100.00"), user.getBalance()); // 余额不应改变
    }

    @Test
    void deductBalance_ShouldWorkWithExactAmount() {
        // Arrange
        user.recharge(new BigDecimal("100.00"));

        // Act
        user.deductBalance(new BigDecimal("100.00"));

        // Assert
        assertEquals(0, user.getBalance().signum());
    }

    @Test
    void deductBalance_ShouldHandlePrecisionCorrectly() {
        // Arrange
        user.recharge(new BigDecimal("100.123456"));

        // Act
        user.deductBalance(new BigDecimal("50.123456"));

        // Assert
        assertEquals(new BigDecimal("50.000000"), user.getBalance());
    }

    @Test
    void combinedOperations_ShouldWorkCorrectly() {
        // Act & Assert
        user.recharge(new BigDecimal("200.00"));
        user.recharge(new BigDecimal("100.50"));
        user.deductBalance(new BigDecimal("150.25"));
        user.deductBalance(new BigDecimal("50.00"));

        assertEquals(new BigDecimal("100.25"), user.getBalance());
    }
}