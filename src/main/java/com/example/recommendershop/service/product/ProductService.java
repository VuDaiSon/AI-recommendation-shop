package com.example.recommendershop.service.product;

import com.example.recommendershop.dto.ApiListBaseRequest;
import com.example.recommendershop.dto.BasePage;
import com.example.recommendershop.dto.SearchEntity;
import com.example.recommendershop.dto.product.request.ProductRequest;
import com.example.recommendershop.dto.product.response.ProductAvatar;
import com.example.recommendershop.dto.product.response.ProductResponse;


import java.util.UUID;

public interface ProductService {
    public ProductResponse create(ProductRequest productRequest);
    public ProductResponse update(UUID productId, ProductRequest productRequest);
    public ProductResponse detail(UUID productId);
    public void delete(UUID productId);
    public BasePage<ProductAvatar> getAll(ApiListBaseRequest listRequest);
    public BasePage<ProductAvatar> search(SearchEntity search, ApiListBaseRequest filter);
    public BasePage<ProductAvatar> getProductsByCategory(UUID categoryId, ApiListBaseRequest listBaseRequest);
}

