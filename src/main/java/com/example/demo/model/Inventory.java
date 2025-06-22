package com.example.demo.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Entity
public class Inventory {
    @Id
    private Long id;
    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    private int quantity;

    public Inventory() {}

    public Inventory(Merchant merchant, Product product, int quantity) {
        this.merchant = merchant;
        this.product  = product;
        this.quantity = quantity;
    }

    public void deductQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity should be greater than 0, quantity: " + quantity);
        }
        if (this.quantity < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        this.quantity = this.quantity - quantity;
    }
}
