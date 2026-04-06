package com.example.recommendershop.mapper;

import com.example.recommendershop.dto.order.request.OrderRequest;
import com.example.recommendershop.dto.order.response.AdminOrderDetailDTO;
import com.example.recommendershop.dto.order.response.OrderResponse;
import com.example.recommendershop.dto.order.response.ProductInOrder;
import com.example.recommendershop.dto.user.response.UserResponse;
import com.example.recommendershop.entity.Order;
import com.example.recommendershop.entity.Product;
import com.example.recommendershop.entity.User;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface OrderMapper {
    UserResponse toResponse(User user);
    List<ProductInOrder> toListDao(List<Product> products);
    Order toEntity(OrderRequest orderRequest);
    List<OrderResponse> toListResponse(List<Order> orders);
    AdminOrderDetailDTO toAdminDTO(Order order);

}
