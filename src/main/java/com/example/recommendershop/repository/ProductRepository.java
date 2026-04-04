package com.example.recommendershop.repository;

import com.example.recommendershop.entity.Category;
import com.example.recommendershop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
        Product findProductByName(String name);
        Page<Product> findProductByCategory(Category category, Pageable pageable);
        List<Product> findByProductIdIn(List<UUID> productIds);

}
