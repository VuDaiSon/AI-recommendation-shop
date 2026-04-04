package com.example.recommendershop.repository;

import com.example.recommendershop.entity.Cart;
import com.example.recommendershop.entity.CartDetail;
import com.example.recommendershop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, UUID>, JpaSpecificationExecutor<CartDetail> {
    CartDetail findByCartAndProduct(Cart cart, Product product);
    List<CartDetail> findByCart(Cart cart);
}
