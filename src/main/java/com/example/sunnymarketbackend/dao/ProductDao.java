package com.example.sunnymarketbackend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sunnymarketbackend.constant.ProductCategory;
import com.example.sunnymarketbackend.entity.Product;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface ProductDao extends BaseMapper<Product> {

    // 所有產品分頁
    @Select("SELECT * FROM product_table")
    Page<Product> selectAllProducts();

    // Category 分頁
    @Select("SELECT * FROM product_table WHERE category = #{category}")
    Page<Product> selectProductsByCategory(ProductCategory category);

    Long addProduct(Product product);

    Product getProductById(Long productId);

    int updateProduct(Product product);

    void deleteProductById(Long productId);

}
