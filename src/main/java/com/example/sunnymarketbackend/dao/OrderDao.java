package com.example.sunnymarketbackend.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sunnymarketbackend.entity.Order;
import com.example.sunnymarketbackend.entity.OrderItem;
import com.github.pagehelper.Page;

@Mapper
public interface OrderDao extends BaseMapper<Order> {
    // 查詢所有訂單
    Page<Order> getAllOrders(Long userId, String sort, String order);
    // 查詢訂單
    Order getOrderById(Long orderId);
    // 查詢訂單明細
    List<OrderItem> getOrderItemsByOrderId(Long orderId);
    // 創建訂單
    Long createOrder(Map<String,Object> orderMap);
    
    // 創建訂單明細
    void createOrderItems(List<OrderItem> orderItemList );



}
