package com.example.recommendershop.dto.order.response;


import com.example.recommendershop.dto.cart.response.CartView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminOrderDetailDTO {
    private UUID orderId;
    private String status;
    private Double totalValue;
    private String receiver;
    private String number;
    private String address;
    private String paymentMethod;
    private Integer shippingFee;
    private CartView cart;
}
