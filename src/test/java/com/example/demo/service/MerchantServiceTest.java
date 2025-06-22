package com.example.demo.service;

import com.example.demo.ModelUtils;
import com.example.demo.dto.MerchantAddInventoryDTO;
import com.example.demo.model.Inventory;
import com.example.demo.model.Merchant;
import com.example.demo.model.Product;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.MerchantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private MerchantService merchantService;

    @Test
    void addInventory_ShouldAddQuantityToExistingInventory() {
        // Arrange
        MerchantAddInventoryDTO dto = new MerchantAddInventoryDTO(1L, 100L, 10);
        Merchant merchant = new Merchant();
        merchant.setId(1L);

        Inventory existingInventory = new Inventory(merchant, ModelUtils.mockProduct(), 5);

        when(merchantRepository.findById(1L)).thenReturn(Optional.of(merchant));
        when(inventoryRepository.findByProductId(100L)).thenReturn(Optional.of(existingInventory));

        // Act
        merchantService.addInventory(dto);

        // Assert
        assertEquals(15, existingInventory.getQuantity());
        verify(inventoryRepository).save(existingInventory);
    }

    @Test
    void addInventory_ShouldCreateNewInventory_WhenNotExists() {
        // Arrange
        MerchantAddInventoryDTO dto = new MerchantAddInventoryDTO(1L, 100L, 10);
        Merchant merchant = new Merchant();
        merchant.setId(1L);

        when(merchantRepository.findById(1L)).thenReturn(Optional.of(merchant));
        when(inventoryRepository.findByProductId(100L)).thenReturn(Optional.empty());

        // Act
        merchantService.addInventory(dto);

        // Assert
        verify(inventoryRepository).save(argThat(inventory ->
                inventory.getMerchant().getId() == 1L &&
                        inventory.getProduct().getId() == 100L &&
                        inventory.getQuantity() == 10
        ));
    }

    @Test
    void addInventory_ShouldThrowException_WhenMerchantNotFound() {
        // Arrange
        MerchantAddInventoryDTO dto = new MerchantAddInventoryDTO(1L, 100L, 10);
        when(merchantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> merchantService.addInventory(dto)
        );

        assertEquals("Merchant not found", exception.getMessage());
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void addInventory_ShouldThrowException_WhenQuantityZeroOrNegative() {
        // Arrange
        MerchantAddInventoryDTO zeroDto = new MerchantAddInventoryDTO(1L, 100L, 0);
        MerchantAddInventoryDTO negativeDto = new MerchantAddInventoryDTO(1L, 100L, -5);

        // Act & Assert
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> merchantService.addInventory(zeroDto),
                        "Quantity must be greater than 0"),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> merchantService.addInventory(negativeDto),
                        "Quantity must be greater than 0")
        );

        verify(merchantRepository, never()).findById(any());
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void addInventory_ShouldThrowException_WhenMerchantProductMismatch() {
        // Arrange
        MerchantAddInventoryDTO dto = new MerchantAddInventoryDTO(1L, 100L, 10);
        Merchant merchant1 = new Merchant();
        merchant1.setId(1L);
        Merchant merchant2 = new Merchant();
        merchant2.setId(2L);

        Inventory inventory = new Inventory(merchant2, ModelUtils.mockProduct(), 5);

        when(merchantRepository.findById(1L)).thenReturn(Optional.of(merchant1));
        when(inventoryRepository.findByProductId(100L)).thenReturn(Optional.of(inventory));

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> merchantService.addInventory(dto)
        );

        assertEquals("Merchant 1 doesn't has product 100", exception.getMessage());
        verify(inventoryRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void addInventory_ShouldRejectInvalidQuantities(int invalidQuantity) {
        MerchantAddInventoryDTO dto = new MerchantAddInventoryDTO(1L, 100L, invalidQuantity);

        assertThrows(IllegalArgumentException.class,
                () -> merchantService.addInventory(dto));
    }

    @Test
    void addInventory_ShouldRollback_WhenExceptionOccurs() {
        MerchantAddInventoryDTO dto = new MerchantAddInventoryDTO(1L, 100L, 10);
        Merchant merchant = new Merchant();
        merchant.setId(1L);

        when(merchantRepository.findById(1L)).thenReturn(Optional.of(merchant));
        when(inventoryRepository.findByProductId(100L)).thenReturn(Optional.empty());
        doThrow(new RuntimeException("DB error")).when(inventoryRepository).save(any());

        assertThrows(RuntimeException.class,
                () -> merchantService.addInventory(dto));
    }

    @Test
    void addInventory_ShouldHandleConcurrentRequests() throws InterruptedException {
        MerchantAddInventoryDTO dto = new MerchantAddInventoryDTO(1L, 100L, 1);
        Merchant merchant = new Merchant();
        merchant.setId(1L);

        Inventory inventory = new Inventory(merchant, ModelUtils.mockProduct(), 0);

        when(merchantRepository.findById(1L)).thenReturn(Optional.of(merchant));
        when(inventoryRepository.findByProductId(100L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        int threadCount = 100;
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            service.execute(() -> merchantService.addInventory(dto));
        }

        service.shutdown();
        assertTrue(service.awaitTermination(1, TimeUnit.MINUTES));

        assertEquals(100, inventory.getQuantity());
    }


}