package com.example.demo.controller;

import com.example.demo.dto.UserRechargeDTO;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@WebFluxTest(controllers = UserController.class)
public class UserControllerTest {

    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Autowired
    private UserController userController;

    private static final String uri = "/api/users/recharge";

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(userController).build();
    }

    @Test
    public void addInventory_ShouldReturnOk() {
        // Arrange
        UserRechargeDTO dto = new UserRechargeDTO(1L, new BigDecimal("10.0"));

        doNothing().when(userService).rechargeUser(any(UserRechargeDTO.class));

        // Act & Assert
        webTestClient.put()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void addInventory_WhenServiceThrowsException_ShouldReturnInternalServerError() {
        // Arrange
        UserRechargeDTO dto = new UserRechargeDTO(1L, new BigDecimal("10.0"));

        doThrow(new RuntimeException("Service error")).when(userService).rechargeUser(any(UserRechargeDTO.class));

        // Act & Assert
        webTestClient.put()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}