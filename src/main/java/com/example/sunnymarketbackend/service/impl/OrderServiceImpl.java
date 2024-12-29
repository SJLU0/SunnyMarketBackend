package com.example.sunnymarketbackend.service.impl;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.sunnymarketbackend.dao.OrderDao;
import com.example.sunnymarketbackend.dao.ProductDao;
import com.example.sunnymarketbackend.dao.UserDao;
import com.example.sunnymarketbackend.dto.BuyItem;
import com.example.sunnymarketbackend.dto.OrderRequest;
import com.example.sunnymarketbackend.entity.Order;
import com.example.sunnymarketbackend.entity.OrderItem;
import com.example.sunnymarketbackend.entity.Product;
import com.example.sunnymarketbackend.entity.User;
import com.example.sunnymarketbackend.service.OrderService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class OrderServiceImpl implements OrderService {

    private final static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;

    @Override
	public PageInfo<Order> getAllOrders(Long userId, Integer pageNum, Integer pageSize, String sort, String order) {
        //分頁
        PageHelper.startPage(pageNum, pageSize);
        //查詢訂單列表
        Page<Order> orderList = orderDao.getAllOrders(userId, sort, order);
        //查詢訂單明細 
        for (Order orders : orderList) {
            Long orderId = orders.getOrderId();
            List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(orderId);
            orders.setOrderItemList(orderItemList);
        }
        return new PageInfo<>(orderList);
	}

    @Override
    public Order getOrderById(Long orderId) {
        Order order = orderDao.getOrderById(orderId);
        List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(orderId);
        order.setOrderItemList(orderItemList);
        return order;
    }
    
    @Transactional
    @Override
    public Long createOrder(Long userId, OrderRequest orderRequest) {
       //檢查 User 是否存在
        User user = userDao.getUserById(userId);
        if (user == null) {
            log.warn("User not found: {}", userId);
            throw new RuntimeException("User not found");
        }
       
        int totalAmount = 0;
        // 訂單明細
        List<OrderItem> orderItemList = new ArrayList<>();
        // 明細資料
        for (BuyItem buyItem : orderRequest.getBuyItemList()) {
            Product product = productDao.getProductById(buyItem.getProductId());
            
            //檢查product 是否存在、庫存是否足夠
            if (product == null) {
                log.warn("Product not found: {}", buyItem.getProductId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            } else if (product.getStock() < buyItem.getQuantity()) {
                log.warn("Product stock is not enough: {}, {}", buyItem.getProductId(), product.getStock(), buyItem.getQuantity());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            //扣除商品庫存
            productDao.updateStock(product.getProductId(), product.getStock() - buyItem.getQuantity(),LocalDateTime.now());


            // 計算總價錢
            int amount = buyItem.getQuantity() * product.getPrice();
            totalAmount += amount;
            // 轉換 BuyItem to OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(buyItem.getProductId());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);
            orderItemList.add(orderItem);
        }

        // 創建訂單 增加新增變數彈性
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("userId", userId);
        orderMap.put("totalAmount", totalAmount);
        orderMap.put("createdDate", LocalDateTime.now());
        orderMap.put("lastModifiedDate", LocalDateTime.now());

        //創建訂單
        orderDao.createOrder(orderMap);
        // 1.從 orderMap 中取得 orderId 並轉換為 BigInteger 類型   
        BigInteger orderIdBigInt = (BigInteger) orderMap.get("orderId");
        // 2.檢查是否為空 
        if (orderIdBigInt == null) {
            throw new RuntimeException("Failed to create order: orderId is null");
        }
        // 3.將 BigInteger 轉換為 Long 類型
        Long orderId = orderIdBigInt.longValue();

        // 設置訂單 ID 並批量插入訂單明細
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(orderId);
        }

        orderDao.createOrderItems(orderItemList);

        return orderId;
    }

}