package com.example.sunnymarketbackend.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class OrderRequest {
    //訂單列表
    @NotEmpty
    private List<BuyItem> buyItemList;

    public List<BuyItem> getBuyItemList() {
        return buyItemList;
    }

    public void setBuyItemList(List<BuyItem> buyItemList) {
        this.buyItemList = buyItemList;
    }


    

}
