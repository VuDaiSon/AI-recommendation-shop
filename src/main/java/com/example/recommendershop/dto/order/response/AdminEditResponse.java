package com.example.recommendershop.dto.order.response;


import java.util.List;

public class AdminEditResponse {
    private final AdminOrderDetailDTO order;    private final List<String> orderStatuses;

    public AdminEditResponse(AdminOrderDetailDTO order, List<String> orderStatuses) {
        this.order = order;
        this.orderStatuses = orderStatuses;
    }

    public AdminOrderDetailDTO getOrder() {
        return order;
    }

    public List<String> getOrderStatuses() {
        return orderStatuses;
    }
}