package com.example.recommendershop.controller;

import com.example.recommendershop.dto.ApiListBaseRequest;
import com.example.recommendershop.dto.BasePage;
import com.example.recommendershop.dto.ResponseData;
import com.example.recommendershop.dto.order.request.OrderRequest;
import com.example.recommendershop.dto.order.response.AdminEditResponse;
import com.example.recommendershop.dto.order.response.CheckOutViewModel;
import com.example.recommendershop.dto.order.response.OrderResponse;
import com.example.recommendershop.entity.Order;
import com.example.recommendershop.service.order.OrderService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/")
    public ResponseData<?> check(){
        return orderService.checkout();
    }
    @PostMapping("/confirm")
    public ResponseData<?> confirm(@RequestBody OrderRequest orderRequest){
        return orderService.confirmOrder(orderRequest);
    }
    @GetMapping()
    public BasePage<OrderResponse> findAllOrder(@ParameterObject ApiListBaseRequest apiListBaseRequest){
        return orderService.getAllOrders(apiListBaseRequest);
    }
    @GetMapping("api/{orderId}")
    public CheckOutViewModel clientDetail(@PathVariable(name = "orderId")UUID orderId){
        return orderService.OrderDetail(orderId);
    }
    @PutMapping("cancel/{orderId}")
    public ResponseData<?> cancel(@PathVariable(name = "orderId") UUID orderId){
        return orderService.cancelOrder(orderId);
    }
    @GetMapping("/adminIndex")
    public BasePage<OrderResponse> AdminIndex(ApiListBaseRequest listBaseRequest){
        return orderService.AdminIndex(listBaseRequest);
    }
    @GetMapping("/admin/edit/{orderId}")
    public AdminEditResponse OrderCheck(@PathVariable(name = "orderId") UUID orderId){
        return orderService.AdminCheck(orderId);
    }
    @PostMapping("/adminEdit/{orderId}")
    public ResponseData<?> OrderEdit(@PathVariable(name = "orderId") UUID orderId, @RequestBody Order order){
        return orderService.AdminEdit(orderId, order);
    }

}
