package com.example.sunnymarketbackend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sunnymarketbackend.constant.ProductCategory;
import com.example.sunnymarketbackend.entity.Product;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface ProductDao extends BaseMapper<Product> {

    // 所有產品分頁
    Page<Product> selectAllProducts(ProductCategory category,
            String sort,
            String order);

    void updateStock(Long productId, Integer stock, LocalDateTime lastModifiedDate);

    Long addProduct(Product product);

    Product getProductById(Long productId);

    int updateProduct(Product product);

    void deleteProductById(Long productId);

}
