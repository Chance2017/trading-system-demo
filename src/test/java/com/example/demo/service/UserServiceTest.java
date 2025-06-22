package com.example.demo.service;

import com.example.demo.dto.UserRechargeDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void rechargeUser_ShouldRechargeSuccessfully_WhenValidInput() {
        // Arrange
        UserRechargeDTO dto = new UserRechargeDTO(1L, new BigDecimal("100.00"));
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setBalance(BigDecimal.ZERO);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        userService.rechargeUser(dto);

        // Assert
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(mockUser);
        assertEquals(new BigDecimal("100.00"), mockUser.getBalance());
    }

    @Test
    void rechargeUser_ShouldThrowIllegalArgumentException_WhenNegativeAmount() {
        // Arrange
        UserRechargeDTO dto = new UserRechargeDTO(1L, new BigDecimal("-50.00"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.rechargeUser(dto)
        );

        assertEquals("amount must be greater than 0", exception.getMessage());
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any());
    }

    @Test
    void rechargeUser_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Arrange
        UserRechargeDTO dto = new UserRechargeDTO(99L, new BigDecimal("100.00"));
        when(userRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.rechargeUser(dto)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void rechargeUser_ShouldHandleZeroAmount() {
        // Arrange
        UserRechargeDTO dto = new UserRechargeDTO(1L, BigDecimal.ZERO);
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setBalance(new BigDecimal("50.00"));

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.rechargeUser(dto)
        );

        assertEquals("Recharge amount should be greater than 0", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void rechargeUser_ShouldAddToExistingBalance() {
        // Arrange
        UserRechargeDTO dto = new UserRechargeDTO(1L, new BigDecimal("100.00"));
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setBalance(new BigDecimal("50.00"));

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        userService.rechargeUser(dto);

        // Assert
        assertEquals(new BigDecimal("150.00"), mockUser.getBalance());
    }

    @Test
    void rechargeUser_ShouldHandlePrecisionCorrectly() {
        // Arrange
        UserRechargeDTO dto = new UserRechargeDTO(1L, new BigDecimal("100.123456"));
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setBalance(new BigDecimal("50.123456"));

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        userService.rechargeUser(dto);

        // Assert
        assertEquals(new BigDecimal("150.246912"), mockUser.getBalance());
    }

    @Test
    void rechargeUser_ShouldRollback_WhenExceptionOccurs() {
        UserRechargeDTO dto = new UserRechargeDTO(1L, new BigDecimal("100.00"));
        User mockUser = mock(User.class);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        doThrow(new RuntimeException("DB error")).when(userRepository).save(any());

        assertThrows(RuntimeException.class,
                () -> userService.rechargeUser(dto));

        // Verify no changes were persisted
        verify(mockUser, never()).setBalance(any());
    }

    @Test
    void rechargeUser_ShouldHandleConcurrentRequests() throws InterruptedException {
        UserRechargeDTO dto = new UserRechargeDTO(1L, new BigDecimal("100.00"));
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setBalance(BigDecimal.ZERO);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int threadCount = 10;
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            service.execute(() -> userService.rechargeUser(dto));
        }

        service.shutdown();
        assertTrue(service.awaitTermination(1, TimeUnit.MINUTES));

        assertEquals(new BigDecimal("1000.00"), mockUser.getBalance());
    }
}