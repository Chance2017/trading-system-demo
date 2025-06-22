package com.example.demo.controller;

import com.example.demo.dto.MerchantAddInventoryDTO;
import com.example.demo.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {
    private MerchantService merchantService;

    @Autowired
    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping("/add-inventory")
    public ResponseEntity<?> addInventory(@RequestBody MerchantAddInventoryDTO dto) {
        merchantService.addInventory(dto);
        return ResponseEntity.ok().build();
    }
}