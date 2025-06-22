package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MerchantTest {

    private Merchant merchant;

    @BeforeEach
    void setUp() {
        merchant = new Merchant();
    }

    @Test
    void earning_ShouldInitializeBalance_WhenBalanceIsNull() {
        // Arrange
        assertNull(merchant.getBalance());

        // Act
        merchant.earning(new BigDecimal("100.50"));

        // Assert
        assertEquals(new BigDecimal("100.50"), merchant.getBalance());
    }

    @Test
    void earning_ShouldAddToExistingBalance_WhenBalanceExists() {
        // Arrange
        merchant.setBalance(new BigDecimal("200.00"));

        // Act
        merchant.earning(new BigDecimal("100.50"));

        // Assert
        assertEquals(new BigDecimal("300.50"), merchant.getBalance());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-100.00"})
    void earning_ShouldThrowException_WhenCashIsZeroOrNegative(String amount) {
        // Arrange
        BigDecimal invalidCash = new BigDecimal(amount);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> merchant.earning(invalidCash)
        );

        assertEquals("Earning cash should be greater than 0", exception.getMessage());
    }

    @Test
    void earning_ShouldHandleDifferentPrecisionsCorrectly() {
        // Arrange
        merchant.setBalance(new BigDecimal("100.000"));

        // Act
        merchant.earning(new BigDecimal("50.123456"));

        // Assert
        assertEquals(new BigDecimal("150.123456"), merchant.getBalance());
    }

    @Test
    void earning_ShouldWorkWithVeryLargeAmounts() {
        // Arrange
        BigDecimal largeAmount = new BigDecimal("999999999999999999.99");

        // Act
        merchant.earning(largeAmount);

        // Assert
        assertEquals(largeAmount, merchant.getBalance());
    }

    @Test
    void earning_ShouldNotChangeBalance_WhenExceptionIsThrown() {
        // Arrange
        merchant.setBalance(new BigDecimal("500.00"));
        BigDecimal originalBalance = merchant.getBalance();

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> merchant.earning(new BigDecimal("-100.00"))
        );

        // Verify balance wasn't changed
        assertEquals(originalBalance, merchant.getBalance());
    }

}