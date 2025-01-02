package com.example.sunnymarketbackend.service;

import com.example.sunnymarketbackend.constant.ProductCategory;
import com.example.sunnymarketbackend.dto.ProductQueryParams;
import com.example.sunnymarketbackend.entity.Product;
import com.example.sunnymarketbackend.dto.ProductRequest;
import com.github.pagehelper.PageInfo;

public interface ProductService {

    PageInfo<Product> selectAllProducts(ProductQueryParams params);

    Long addProduct(ProductRequest productRequest);

    Product getProductById(Long productId);

    void updateProduct(Long productId, ProductRequest productRequest);

    void deleteProductById(Long productId);

}
