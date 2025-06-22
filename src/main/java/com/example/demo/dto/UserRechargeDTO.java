package com.example.demo.dto;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class UserRechargeDTO {
    @NonNull
    private Long userId;
    @NonNull
    private BigDecimal amount;

    public UserRechargeDTO() {}

    public UserRechargeDTO(@NonNull Long userId, @NonNull BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
