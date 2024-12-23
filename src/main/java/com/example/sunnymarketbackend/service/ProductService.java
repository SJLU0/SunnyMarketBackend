package com.example.sunnymarketbackend.service;

import com.example.sunnymarketbackend.entity.Product;
import com.example.sunnymarketbackend.dto.ProductRequest;

public interface ProductService {

    Long addProduct(ProductRequest productRequest);

    Product getProductById(Long productId);

    // void updateProduct(Long productId, ProductRequest productRequest);

}
