package com.example.sunnymarketbackend.service.impl;

import com.example.sunnymarketbackend.constant.ProductCategory;
import com.example.sunnymarketbackend.dto.ProductRequest;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sunnymarketbackend.dao.ProductDao;
import com.example.sunnymarketbackend.entity.Product;
import com.example.sunnymarketbackend.service.ProductService;

import java.time.LocalDateTime;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Override
    public PageInfo<Product> getAllProductsWithPaginationNew(Integer pageNum,
            Integer pageSize,
            ProductCategory category,
            String sort,
            String order) {
        PageHelper.startPage(pageNum, pageSize);

        Page<Product> productList = productDao.selectAllProducts(category, order, sort);

        return new PageInfo<>(productList);
    }

    @Override
    public Long addProduct(ProductRequest productRequest) {
        Product newProduct = new Product();
        newProduct.setProductName(productRequest.getProductName());
        newProduct.setCategory(productRequest.getCategory());
        newProduct.setPrice(productRequest.getPrice());
        newProduct.setStock(productRequest.getStock());
        newProduct.setDescription(productRequest.getDescription());
        newProduct.setImageUrl(productRequest.getImageUrl());
        newProduct.setCreatedDate(LocalDateTime.now());
        newProduct.setLastModifiedDate(LocalDateTime.now());
        productDao.addProduct(newProduct);

        return newProduct.getProductId();
    }

    @Override
    public Product getProductById(Long productId) {
        return productDao.getProductById(productId);
    }

    @Override
    public void updateProduct(Long productId, ProductRequest productRequest) {

        // 更新產品資料
        Product existingProduct = new Product();

        existingProduct.setProductId(productId);
        existingProduct.setCategory(productRequest.getCategory());
        existingProduct.setProductName(productRequest.getProductName());
        existingProduct.setImageUrl(productRequest.getImageUrl());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setStock(productRequest.getStock());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setLastModifiedDate(LocalDateTime.now());

        int result = productDao.updateProduct(existingProduct);

        if (result == 0) {
            throw new RuntimeException("Failed to update product: " + productId);
        }
    }

    @Override
    public void deleteProductById(Long productId) {
        productDao.deleteProductById(productId);
    }
}
