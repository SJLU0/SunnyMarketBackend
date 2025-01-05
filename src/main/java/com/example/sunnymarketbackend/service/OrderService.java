package com.example.sunnymarketbackend.service;

import com.example.sunnymarketbackend.dto.OrderRequest;
import com.example.sunnymarketbackend.entity.Order;
import com.github.pagehelper.PageInfo;

public interface OrderService {

    //查詢所有訂單 
    PageInfo<Order> getAllOrders(Long userId, Integer pageNum, Integer pageSize, String sort, String order);
    //創建訂單
    Long createOrder(Long userId, OrderRequest orderRequest);
    //查詢訂單
    Order getOrderById(Long orderId);
    //刪除訂單與明細
    void deleteOrder(Long orderId);
    //刪除單筆訂單明細
    void deleteOrderItem(Long orderItemId);
    


}
