package com.example.demo.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
public class Merchant {
    @Id
    private Long id;
    private String name;
    private BigDecimal balance;
    private BigDecimal balanceOfPreviousDay;

    public void earning(BigDecimal cash) {
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
        if (cash.signum() <= 0) {
            throw new IllegalArgumentException("Earning cash should be greater than 0");
        }
        this.balance = this.balance.add(cash);
    }
}