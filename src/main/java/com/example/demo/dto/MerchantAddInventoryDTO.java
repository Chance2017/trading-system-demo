package com.example.demo.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class MerchantAddInventoryDTO {
    @NonNull
    private Long merchantId;
    @NonNull
    private Long productId;
    @NonNull
    private Integer quantity;

    public MerchantAddInventoryDTO() {}

    public MerchantAddInventoryDTO(@NonNull Long merchantId, @NonNull Long productId, @NonNull Integer quantity) {
        this.merchantId = merchantId;
        this.productId  = productId;
        this.quantity   = quantity;
    }
}
