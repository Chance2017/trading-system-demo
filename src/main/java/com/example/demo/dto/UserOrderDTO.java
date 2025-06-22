package com.example.demo.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserOrderDTO {
    @NonNull
    private Long userId;
    @NonNull
    private Long productId;
    @NonNull
    private Integer quantity;

    public UserOrderDTO() {}

    public UserOrderDTO(@NonNull Long userId, @NonNull Long productId, @NonNull Integer quantity) {
        this.userId    = userId;
        this.productId = productId;
        this.quantity  = quantity;
    }
}
