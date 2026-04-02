package com.example.recommendershop.controller;


import com.example.recommendershop.dto.ResponseData;
import com.example.recommendershop.dto.cart.request.AddToCartRequest;
import com.example.recommendershop.dto.cart.response.CartView;
import com.example.recommendershop.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/carts")
public class CartController {
    @Autowired
    private CartService cartService;
    @PostMapping("/addToCart")
    public ResponseData<?> addToCart(@RequestBody AddToCartRequest addToCartRequest){
        return cartService.addToCart(addToCartRequest);
    }
    @PatchMapping("/updateCart")
    public ResponseData<?> editCart(@RequestBody Map<String, Object> data){
        return cartService.updateCart(data);
    }
    @DeleteMapping("/{cartDetailId}")
    public ResponseData<?> RemoveCartLine(@PathVariable(name = "cartDetailId")UUID cartDetailId){
        return cartService.deleteCartLine(cartDetailId);
    }
    @GetMapping
    public CartView cartCheck(){
        return cartService.CartCheck();
    }
}
