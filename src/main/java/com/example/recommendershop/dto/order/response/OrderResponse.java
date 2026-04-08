package com.example.recommendershop.dto.order.response;

import com.example.recommendershop.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse extends BaseDto {
    private UUID orderId;
    private LocalDateTime date;
    private Double totalValue;
    private String status;
    private String address;
    private String number;
    private String receiver;
    private Integer shippingFee;
    private String paymentMethod;
}
