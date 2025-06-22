package com.example.demo.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
public class User {
    @Id
    private Long id;
    private String name;
    private BigDecimal balance;

    public void recharge(BigDecimal amount) {
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Recharge amount should be greater than 0");
        }
        this.balance = this.balance.add(amount);
    }

    public void deductBalance(BigDecimal amount) {
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Consume amount should be greater than 0");
        }

        if (this.balance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        this.balance = this.balance.subtract(amount);
    }
}