package com.example.recommendershop.mapper;

import com.example.recommendershop.dto.cartDetail.CartDetailResponse;
import com.example.recommendershop.entity.CartDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartDetailMapper {

    CartDetailResponse toDao(CartDetail cartDetail);

}