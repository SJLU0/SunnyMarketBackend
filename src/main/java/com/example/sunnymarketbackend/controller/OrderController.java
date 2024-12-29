package com.example.sunnymarketbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sunnymarketbackend.dto.OrderRequest;
import com.example.sunnymarketbackend.entity.Order;
import com.example.sunnymarketbackend.service.OrderService;
import com.github.pagehelper.PageInfo;

import jakarta.validation.Valid;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    //查詢訂單
    @GetMapping("/users/{userId}/getAllOrders")
    public ResponseEntity<PageInfo<Order>> getAllOrders(
            @PathVariable Long userId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sort", defaultValue = "createdDate") String sort,
            @RequestParam(value = "order", defaultValue = "DESC") String order) {
        PageInfo<Order> orderList = orderService.getAllOrders(userId, pageNum, pageSize, sort, order);
        return ResponseEntity.ok(orderList);
    }

    //新增訂單&明細
    @PostMapping("/users/{userId}/createOrder")
    public ResponseEntity<?> createOrder(@PathVariable Long userId, 
                                         @RequestBody @Valid OrderRequest orderRequest) {
        //新增主檔回傳  orderId                                  
        Long orderId = orderService.createOrder(userId, orderRequest);
        //返回整筆訂單
        Order order = orderService.getOrderById(orderId);
                        
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

}
