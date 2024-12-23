package com.example.sunnymarketbackend.controller;

import com.example.sunnymarketbackend.dto.ProductRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.sunnymarketbackend.entity.Product;
import com.example.sunnymarketbackend.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/addProduct") // 新增商品
    public ResponseEntity<Product> addProduct(@RequestBody @Valid ProductRequest productRequest) {
        Long productId = productService.addProduct(productRequest);
        Product product = productService.getProductById(productId);
        if (product != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById (@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PutMapping("updateProduct/{productId}")
    public ResponseEntity<Product> updateProduct (@PathVariable Long productId,
                                                  @RequestBody ProductRequest productRequest){
        //檢查 product 是否存在
        Product product = productService.getProductById(productId);
        if(product == null){
            return ResponseEntity.notFound().build();
        }
        // 修改商品的數據
        productService.updateProduct(productId, productRequest);
        // 取得更新後的商品資訊
        Product updateProduct = productService.getProductById(productId);
        return ResponseEntity.status(HttpStatus.OK).body(updateProduct);
    }

    @DeleteMapping("deleteProduct/{productId}")
    public ResponseEntity<?> deleteProductById (@PathVariable Long productId){
        // 刪除商品
        productService.deleteProductById(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
