package com.example.sunnymarketbackend.dto;

import jakarta.validation.constraints.NotNull;
//訂單產品數量
public class BuyItem {

    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
   
}

