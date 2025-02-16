package com.example.sunnymarketbackend.controller;

import com.example.sunnymarketbackend.constant.ProductCategory;
import com.example.sunnymarketbackend.dto.ProductQueryParams;
import com.example.sunnymarketbackend.dto.ProductRequest;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.sunnymarketbackend.entity.Product;
import com.example.sunnymarketbackend.service.ProductService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/getAllProducts")
    public ResponseEntity<PageInfo<Product>> getAllProducts(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "category", required = false) ProductCategory category,
            @RequestParam(value = "sort", defaultValue = "createDate") String sort,
            @RequestParam(value = "order", defaultValue = "DESC") String order,
            @RequestParam(value = "search", required = false) String search) {
        ProductQueryParams params = new ProductQueryParams();
        params.setPageNum(pageNum);
        params.setPageSize(pageSize);
        params.setCategory(category);
        params.setSort(sort);
        params.setOrder(order);
        params.setSearch(search);
        PageInfo<Product> productList = productService.selectAllProducts(params);
        return ResponseEntity.ok(productList);
    }

    @PostMapping("/admin/addProduct")
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
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/admin/updateProduct/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId,
            @RequestBody ProductRequest productRequest) {
        // 檢查 product 是否存在
        Product product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        // 修改商品的數據
        productService.updateProduct(productId, productRequest);
        // 取得更新後的商品資訊
        Product updateProduct = productService.getProductById(productId);
        return ResponseEntity.status(HttpStatus.OK).body(updateProduct);
    }

    @DeleteMapping("/admin/deleteProduct/{productId}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long productId) {
        // 刪除商品
        productService.deleteProductById(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
