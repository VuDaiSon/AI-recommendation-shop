package com.example.recommendershop.controller;

import com.example.recommendershop.dto.ApiListBaseRequest;
import com.example.recommendershop.dto.BasePage;
import com.example.recommendershop.dto.SearchEntity;
import com.example.recommendershop.dto.product.request.ProductRequest;
import com.example.recommendershop.dto.product.response.ProductAvatar;
import com.example.recommendershop.dto.product.response.ProductResponse;
import com.example.recommendershop.service.product.ProductService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;


@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ProductResponse add(@RequestBody ProductRequest productRequest) {
        return productService.create(productRequest);
    }
    @PutMapping("/{productId}")
    public ProductResponse edit(@PathVariable(name = "productId")UUID productId, @RequestBody ProductRequest productRequest){
        return productService.update(productId, productRequest);
    }
    @DeleteMapping("/{productId}")
    public void delete(@PathVariable(name = "productId") UUID productId){
        productService.delete(productId);
    }
    @GetMapping("/{productId}")
    public ProductResponse getById(@PathVariable(name = "productId") UUID productId){
        return productService.detail(productId);
    }
    @CrossOrigin
    @GetMapping("/")
    public BasePage<ProductAvatar> findAll(@ParameterObject ApiListBaseRequest listRequest){
        return productService.getAll(listRequest);
    }
    @GetMapping("/search")
    public BasePage<ProductAvatar> search(@ParameterObject SearchEntity search, @ParameterObject ApiListBaseRequest filter){
        return productService.search(search, filter);
    }
    @GetMapping("/category/{categoryId}")
    public BasePage<ProductAvatar> findProductByCategory(@PathVariable(name = "categoryId") UUID categoryId, @ParameterObject ApiListBaseRequest listBaseRequest){
        return productService.getProductsByCategory(categoryId, listBaseRequest);
    }
}
