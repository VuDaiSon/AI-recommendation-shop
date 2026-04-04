package com.example.recommendershop.dto.order.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Double totalValue;
    private String address;
    private String number;
    private String receiver;
    private Integer shippingFee;
    private String paymentMethod;
    private UUID cartId;
    private UUID userId;
}
