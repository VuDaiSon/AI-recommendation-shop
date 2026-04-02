package com.example.recommendershop.mapper;

import com.example.recommendershop.dto.cart.response.CartResponse;
import com.example.recommendershop.dto.cart.response.CartView;
import com.example.recommendershop.entity.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CartDetailMapper.class)
public interface CartMapper {
    CartResponse toDao(Cart cart);
    CartView toResponse(Cart cart);
}
