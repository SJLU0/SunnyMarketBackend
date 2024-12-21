package com.example.sunnymarketbackend.service.impl;

import com.example.sunnymarketbackend.dto.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sunnymarketbackend.dao.ProductDao;
import com.example.sunnymarketbackend.entity.Product;
import com.example.sunnymarketbackend.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Override
    public Long addProduct(ProductRequest productRequest) {
        Product newProduct = new Product();
        newProduct.setProductName(productRequest.getProductName());
        newProduct.setCategory(productRequest.getCategory());
        newProduct.setPrice(productRequest.getPrice());
        newProduct.setStock(productRequest.getStock());
        newProduct.setDescription(productRequest.getDescription());
        newProduct.setImageUrl(productRequest.getImageUrl());
        productDao.addProduct(newProduct);

        return newProduct.getProductId();
    }

    @Override
    public Product getProductById(Long productId) {
        return productDao.getProductById(productId);
    }

}
