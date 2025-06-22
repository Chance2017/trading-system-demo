package com.example.demo.controller;

import com.example.demo.dto.MerchantAddInventoryDTO;
import com.example.demo.service.MerchantService;
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

@WebFluxTest(controllers = MerchantController.class)
public class MerchantControllerTest {

    private WebTestClient webTestClient;

    @MockBean
    private MerchantService merchantService;

    @Autowired
    private MerchantController merchantController;

    private static final String uri = "/api/merchants/add-inventory";

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(merchantController).build();
    }

    @Test
    public void addInventory_ShouldReturnOk() {
        // Arrange
        MerchantAddInventoryDTO dto = new MerchantAddInventoryDTO(1L, 100L, 10);

        doNothing().when(merchantService).addInventory(any(MerchantAddInventoryDTO.class));

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
        MerchantAddInventoryDTO dto = new MerchantAddInventoryDTO(1L, 100L, 10);

        doThrow(new RuntimeException("Service error")).when(merchantService).addInventory(any(MerchantAddInventoryDTO.class));

        // Act & Assert
        webTestClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}