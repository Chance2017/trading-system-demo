package com.example.demo.service;

import com.example.demo.dto.MerchantAddInventoryDTO;
import com.example.demo.model.Inventory;
import com.example.demo.model.Merchant;
import com.example.demo.model.Product;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

@Service
@Transactional
public class MerchantService {
    private MerchantRepository merchantRepository;
    private InventoryRepository inventoryRepository;

    @Autowired
    public MerchantService(MerchantRepository merchantRepository, InventoryRepository inventoryRepository) {
        this.merchantRepository  = merchantRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public void addInventory(MerchantAddInventoryDTO dto) {
        if (dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Merchant merchant = merchantRepository.findById(dto.getMerchantId())
                .orElseThrow(() -> new NotFoundException("Merchant not found"));

        Inventory inventory = inventoryRepository.findByProductId(dto.getProductId())
                .orElseGet(() -> {
                    Product product = new Product();
                    product.setId(dto.getProductId());
                    return new Inventory(merchant, product, 0);
                });

        if (!inventory.getMerchant().getId().equals(dto.getMerchantId())) {
            throw new NotFoundException("Merchant " + dto.getMerchantId() + " doesn't has product " + dto.getProductId());
        }

        inventory.setQuantity(inventory.getQuantity() + dto.getQuantity());
        inventoryRepository.save(inventory);
    }
}
