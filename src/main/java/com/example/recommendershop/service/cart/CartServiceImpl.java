package com.example.recommendershop.service.cart;

import com.example.recommendershop.dto.ResponseData;
import com.example.recommendershop.dto.cart.request.AddToCartRequest;
import com.example.recommendershop.dto.cart.response.CartView;
import com.example.recommendershop.entity.Cart;
import com.example.recommendershop.entity.CartDetail;
import com.example.recommendershop.entity.Product;
import com.example.recommendershop.exception.MasterException;
import com.example.recommendershop.mapper.CartMapper;
import com.example.recommendershop.repository.CartDetailRepository;
import com.example.recommendershop.repository.CartRepository;
import com.example.recommendershop.repository.ProductRepository;
import com.example.recommendershop.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartServiceImpl implements CartService{
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final HttpSession httpSession;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartDetailRepository cartDetailRepository, HttpSession httpSession, ProductRepository productRepository, UserRepository userRepository, CartMapper cartMapper){
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.httpSession = httpSession;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    public ResponseData<?> addToCart(AddToCartRequest addToCartRequest) {
        UUID userId = (UUID) httpSession.getAttribute("UserId");
        if(userId == null){
            throw new MasterException(HttpStatus.UNAUTHORIZED, "bạn cần đăng nhập để thực hiện chức năng này");
        }
        Optional<Cart> optionalCart = cartRepository.findCartByUser_UserIdAndStatus(userId, "Active");
        Cart cart;

        if (optionalCart.isEmpty()) {
            cart = new Cart();
            cart.setUser(userRepository.findById(userId).orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "User not found!")));
            cart.setStatus("Active");
            cartRepository.save(cart);
        } else {
            cart = optionalCart.get();
        }
        UUID productId = addToCartRequest.getProductId();
        int quantity = addToCartRequest.getQuantity();
        Product product = productRepository.findById(productId).orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Product not found"));
        CartDetail existingCartDetail = cartDetailRepository.findByCartAndProduct(cart, product);

        if (existingCartDetail != null) {
            existingCartDetail.setQuantity(existingCartDetail.getQuantity() + quantity);
            cartDetailRepository.save(existingCartDetail);
        } else {
            CartDetail newCartDetail = new CartDetail();
            newCartDetail.setCart(cart);
            newCartDetail.setProduct(product);
            newCartDetail.setQuantity(quantity);
            cartDetailRepository.save(newCartDetail);
        }

        return new ResponseData<>(HttpStatus.OK.value(),"Quantity: " + quantity + " and ProductId: " + productId);
    }

    @Override
    public ResponseData<?> updateCart(Map<String, Object> data) {
        // Parse cartDetailId and quantity from request body
        UUID cartDetailId = UUID.fromString(data.get("cartDetailId").toString());
        int quantity = Integer.parseInt(data.get("quantity").toString());

        // Find the CartDetail by id
        Optional<CartDetail> optionalCartDetail = cartDetailRepository.findById(cartDetailId);
        if (optionalCartDetail.isEmpty()) {
            // If not found, return 404 Not Found
            throw new MasterException(HttpStatus.NOT_FOUND, "CartDetail not found!");
        }

        // If found, update the quantity
        CartDetail cartDetail = optionalCartDetail.get();
        cartDetail.setQuantity(cartDetail.getQuantity() + quantity);

        // Ensure quantity is at least 1
        if (cartDetail.getQuantity() == 0) {
            cartDetail.setQuantity(1);
        }

        // Save the updated CartDetail
        cartDetailRepository.save(cartDetail);

        // Return success response
        return new ResponseData<>(HttpStatus.OK.value(),"update thành công");
    }
    public ResponseData<?> deleteCartLine(UUID cartDetailId){
        CartDetail cartDetail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "không tìm thấy!"));

        cartDetailRepository.delete(cartDetail);
        return new ResponseData<>(HttpStatus.OK.value(),"xóa thành công");
    }

    @Override
    public CartView CartCheck() {
        UUID userId = (UUID) httpSession.getAttribute("UserId");
        Optional<Cart> cartOptional = cartRepository.findCartByUser_UserIdAndStatus(userId, "Active");
        Cart cart = cartOptional.get();
        return cartMapper.toResponse(cart);
    }
}
