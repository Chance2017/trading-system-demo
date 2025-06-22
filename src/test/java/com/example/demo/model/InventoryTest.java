package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.setQuantity(100);
    }

    @Test
    void deductQuantity_ShouldDecreaseQuantity_WhenSufficientStock() {
        int quantityToDeduct = 30;

        // Act
        inventory.deductQuantity(quantityToDeduct);

        // Assert
        assertEquals(70, inventory.getQuantity());
    }

    @Test
    void deductQuantity_ShouldThrowException_WhenInsufficientStock() {
        // Arrange
        int quantityToDeduct = 150;

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventory.deductQuantity(quantityToDeduct);
        });

        assertEquals("Insufficient stock", exception.getMessage());
        assertEquals(100, inventory.getQuantity());
    }

    @Test
    void deductQuantity_ShouldWorkCorrectly_WhenDeductingAllStock() {
        // Arrange
        int quantityToDeduct = 100;

        // Act
        inventory.deductQuantity(quantityToDeduct);

        // Assert
        assertEquals(0, inventory.getQuantity());
    }

    @Test
    void deductQuantity_ShouldThrowException_WhenDeductingZero() {
        // Arrange
        int quantityToDeduct = 0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            inventory.deductQuantity(quantityToDeduct);
        });
    }

    @Test
    void deductQuantity_ShouldThrowException_WhenDeductingNegative() {
        // Arrange
        int quantityToDeduct = -10;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            inventory.deductQuantity(quantityToDeduct);
        });
    }
}