package com.example.sunnymarketbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.sunnymarketbackend.dto.OrderRequest;
import com.example.sunnymarketbackend.entity.Order;
import com.example.sunnymarketbackend.service.OrderService;
import com.github.pagehelper.PageInfo;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    //
    //後台查詢所有訂單&明細
    @GetMapping("/getAllOrders")
    public ResponseEntity<PageInfo<Order>> getAllOrders(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sort", defaultValue = "createdDate") String sort,
            @RequestParam(value = "order", defaultValue = "DESC") String order,
            @RequestParam(required = false) String search) {
            OrderRequest params = new OrderRequest();
            params.setPageNum(pageNum);
            params.setPageSize(pageSize);
            params.setSort(sort);
            params.setOrder(order);
            params.setUserId(userId);
            params.setSearch(search);
        PageInfo<Order> orderList = orderService.getAllOrders(params);
        return ResponseEntity.ok(orderList);
    }

    //新增訂單&明細
    @PostMapping("{userId}/createOrder")
    public ResponseEntity<?> createOrder(@PathVariable Long userId,
                                         @RequestBody @Valid OrderRequest orderRequest) {
        //新增主檔回傳  orderId                                  
        Long orderId = orderService.createOrder(userId, orderRequest);
        //返回整筆訂單
        Order order = orderService.getOrderById(orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    //刪除訂單與訂單明細
    @DeleteMapping("{userId}/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long userId, @PathVariable Long orderId) {
       try{
        orderService.deleteOrder(orderId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
       }
       catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除訂單失敗" + e.getMessage());
       }
        
    }
    //刪除單筆訂單明細
    @DeleteMapping("{userId}/{orderId}/orderItems/{orderItemId}")
    public ResponseEntity<?> deleteOrderItem(@PathVariable Long userId, @PathVariable Long orderId, @PathVariable Long orderItemId) {
       try {
        orderService.deleteOrderItem(orderItemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        
       } 
       catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除訂單明細失敗" + e.getMessage());
        }
    }

}
