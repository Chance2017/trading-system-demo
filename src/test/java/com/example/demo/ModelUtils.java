package com.example.demo;

import com.example.demo.model.Inventory;
import com.example.demo.model.Merchant;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@UtilityClass
public class ModelUtils {

    public Inventory mockInventory() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setMerchant(mockMerchant());
        inventory.setProduct(mockProduct());
        inventory.setQuantity(1);
        return inventory;
    }

    public Merchant mockMerchant() {
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setName("merchant");
        merchant.setBalance(new BigDecimal("10.0"));
        merchant.setBalanceOfPreviousDay(new BigDecimal("0.0"));
        return merchant;
    }

    public Product mockProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("product");
        product.setMerchant(mockMerchant());
        product.setPrice(new BigDecimal("10.5"));
        return product;
    }

    public User mockUser() {
        User user = new User();
        user.setId(1L);
        user.setName("user");
        user.setBalance(new BigDecimal("100.0"));
        return user;
    }

    public Order mockOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setProduct(mockProduct());
        order.setStatus(OrderStatus.COMPLETED);
        order.setTotalAmount(new BigDecimal("21.0"));
        order.setQuantity(2);
        order.setUser(mockUser());
        order.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return order;
    }

}
