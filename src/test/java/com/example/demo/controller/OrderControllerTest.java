package com.example.demo.controller;

import com.example.demo.dto.UserOrderDTO;
import com.example.demo.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@WebFluxTest(controllers = OrderController.class)
public class OrderControllerTest {

    private WebTestClient webTestClient;

    @MockBean
    private OrderService orderService;

    @Autowired
    private OrderController orderController;

    private static final String uri = "/api/orders";

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(orderController).build();
    }

    @Test
    public void addInventory_ShouldReturnOk() {
        // Arrange
        UserOrderDTO dto = new UserOrderDTO(1L, 1L, 10);

        doNothing().when(orderService).createOrder(any(UserOrderDTO.class));

        // Act & Assert
        webTestClient.post()
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
        UserOrderDTO dto = new UserOrderDTO(1L, 1L, 10);

        doThrow(new RuntimeException("Service error")).when(orderService).createOrder(any(UserOrderDTO.class));

        // Act & Assert
        webTestClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}