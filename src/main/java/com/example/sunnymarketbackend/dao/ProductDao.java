package com.example.sunnymarketbackend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sunnymarketbackend.entity.Product;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductDao extends BaseMapper<Product> {

    Long addProduct(Product product);

    Product getProductById(Long productId);

    int updateProduct(Product product);

    void deleteProductById(Long productId);

}
