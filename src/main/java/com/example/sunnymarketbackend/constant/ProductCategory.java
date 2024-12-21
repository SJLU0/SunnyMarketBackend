package com.example.sunnymarketbackend.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum ProductCategory {
    FOOD("FOOD"),
    SEAFOOD("SEAFOOD");

    @EnumValue //給MyBatis Plus 映射到資料庫
    private final String category;
    ProductCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

}
