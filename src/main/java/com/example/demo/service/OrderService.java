package com.example.demo.service;

import com.example.demo.dto.UserOrderDTO;
import com.example.demo.model.Inventory;
import com.example.demo.model.Merchant;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.User;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.MerchantRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.math.BigDecimal;

@Service
@Transactional
public class OrderService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private OrderRepository orderRepository;

    public void createOrder(UserOrderDTO dto) {
        if (dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Inventory inventory = inventoryRepository.findByProductId(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not in stock"));

        BigDecimal totalAmount = inventory.getProduct().getPrice().multiply(new BigDecimal(dto.getQuantity()));

        user.deductBalance(totalAmount);

        Merchant merchant = inventory.getMerchant();
        merchant.earning(totalAmount);

        inventory.deductQuantity(dto.getQuantity());

        Order order = new Order(user, inventory.getProduct(), dto.getQuantity(), totalAmount, OrderStatus.COMPLETED);

        userRepository.save(user);
        merchantRepository.save(merchant);
        inventoryRepository.save(inventory);
        orderRepository.save(order);
    }
}
