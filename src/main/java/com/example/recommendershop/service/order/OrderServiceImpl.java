package com.example.recommendershop.service.order;

import com.example.recommendershop.authorization.PermissionCheck;
import com.example.recommendershop.dto.ApiListBaseRequest;
import com.example.recommendershop.dto.BasePage;
import com.example.recommendershop.dto.ResponseData;
import com.example.recommendershop.dto.cartDetail.CartDetailResponse;
import com.example.recommendershop.dto.order.request.OrderRequest;
import com.example.recommendershop.dto.cart.response.CartResponse;
import com.example.recommendershop.dto.order.response.*;
import com.example.recommendershop.dto.user.response.UserResponse;
import com.example.recommendershop.entity.*;
import com.example.recommendershop.exception.MasterException;
import com.example.recommendershop.mapper.CartMapper;
import com.example.recommendershop.mapper.OrderMapper;
import com.example.recommendershop.repository.*;
import com.example.recommendershop.utils.FilterDataUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private final HttpSession httpSession;
    private final UserRepository userRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartMapper cartMapper;
    private final PermissionCheck permissionCheck;

    public OrderServiceImpl(HttpSession httpSession,
                            UserRepository userRepository,
                            CartDetailRepository cartDetailRepository,
                            ProductRepository productRepository,
                            OrderMapper orderMapper,
                            CartRepository cartRepository,
                            OrderRepository orderRepository,
                            CartMapper cartMapper,
                            PermissionCheck permissionCheck) {
        this.httpSession = httpSession;
        this.userRepository = userRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.cartMapper = cartMapper;
        this.permissionCheck = permissionCheck;
    }

    public int CalculateShippingFee(int totalValue) {
        int shippingFee;
        if (totalValue < 3000000) {
            shippingFee = 15000;
        } else if (totalValue < 5000000) {
            shippingFee = 20000;
        } else if (totalValue < 7500000) {
            shippingFee = 30000;
        } else shippingFee = 0;
        return shippingFee;
    }

    @Override
    public ResponseData<?> checkout() {
        UUID userId = (UUID) httpSession.getAttribute("UserId");
        if (userId == null) {
            throw new MasterException(HttpStatus.UNAUTHORIZED, "bạn cần đăng nhập để thực hiện chức năng này");
        }

        Optional<Cart> optionalCart = cartRepository.findCartByUser_UserIdAndStatus(userId, "Active");

        if (optionalCart.isEmpty()) {
            throw new MasterException(HttpStatus.BAD_REQUEST, "giỏ hàng trống, hãy thêm sản phẩm vào giỏ hàng để tiến hành thanh toán");
        }

        Cart cart = optionalCart.get();
        CartResponse cartResponse = cartMapper.toDao(cart);

        User user = userRepository.getByUserId(userId);
        UserResponse userResponse = orderMapper.toResponse(user);

        List<CartDetail> cartDetails = cartDetailRepository.findByCart(cart);

        List<UUID> productId = cartDetails.stream()
                .map(cartDetail -> cartDetail.getProduct().getProductId())
                .collect(Collectors.toList());

        List<Product> products = productRepository.findByProductIdIn(productId);

        int subtotal = 0;
        for (int i = 0; i < cartDetails.size(); i++) {
            subtotal += cartDetails.get(i).getQuantity() * products.get(i).getPrice();
        }

        List<CartDetailResponse> cartDetailResponses = cartDetails.stream().map(cartDetail -> {
            Product product = cartDetail.getProduct();
            ProductInOrder productInOrder = new ProductInOrder(product.getName(), product.getPrice(), product.getMainImage());
            return new CartDetailResponse(
                    cartDetail.getCartDetailId(),
                    cartDetail.getQuantity(),
                    productInOrder
            );
        }).collect(Collectors.toList());

        int shippingFee = CalculateShippingFee(subtotal);

        CheckOutViewModel viewModel = new CheckOutViewModel(userResponse, cartResponse, cartDetailResponses, subtotal, shippingFee);
        return new ResponseData<>(HttpStatus.OK.value(), "chuyển đến trang thanh toán", viewModel);
    }

    @Override
    public ResponseData<?> confirmOrder(OrderRequest orderRequest) {

        Cart cart = cartRepository.findById(orderRequest.getCartId())
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Cart không tồn tại"));

        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "User không tồn tại"));

        if (orderRequest.getPaymentMethod() == null) {
            throw new MasterException(HttpStatus.BAD_REQUEST, "Vui lòng chọn phương thức thanh toán");
        }

        Order order = orderMapper.toEntity(orderRequest);
        order.setStatus("Chờ xác nhận");
        order.setCart(cart);
        order.setUser(user);

        orderRepository.save(order);

        cart.setStatus("Checked out");
        cartRepository.save(cart);

        return new ResponseData<>(HttpStatus.OK.value(), "đặt hàng thành công");
    }

    protected BasePage<OrderResponse> map(Page<Order> page) {
        BasePage<OrderResponse> rPage = new BasePage<>();
        rPage.setData(orderMapper.toListResponse(page.getContent()));
        rPage.setTotalPage(page.getTotalPages());
        rPage.setTotalRecord(page.getTotalElements());
        rPage.setPage(page.getPageable().getPageNumber());
        return rPage;
    }

    @Override
    public BasePage<OrderResponse> getAllOrders(ApiListBaseRequest apiListBaseRequest) {
        UUID userId = (UUID) httpSession.getAttribute("UserId");
        if (userId == null) {
            throw new MasterException(HttpStatus.FORBIDDEN, "Bạn cần đăng nhập để thực hiện chức năng này");
        }

        Page<Order> page = orderRepository.findByUser_UserId(userId, FilterDataUtil.buildPageRequest(apiListBaseRequest));
        return this.map(page);
    }

    public CheckOutViewModel OrderDetail(UUID orderId) {
        if (orderId == null) {
            throw new MasterException(HttpStatus.NOT_FOUND, "không tìm thấy đơn đặt hàng");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "không tìm thấy đơn đặt hàng"));

        UUID userId = (UUID) httpSession.getAttribute("UserId");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "User không tồn tại"));

        Cart cart = cartRepository.findByUser_UserIdAndCartId(userId, order.getCart().getCartId())
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Cart không tồn tại"));

        List<CartDetail> cartDetails = cartDetailRepository.findByCart(cart);

        CartResponse cartResponse = cartMapper.toDao(cart);
        UserResponse userResponse = orderMapper.toResponse(user);

        List<UUID> productId = cartDetails.stream()
                .map(cartDetail -> cartDetail.getProduct().getProductId())
                .collect(Collectors.toList());

        List<Product> products = productRepository.findByProductIdIn(productId);

        int subtotal = 0;
        for (int i = 0; i < cartDetails.size(); i++) {
            subtotal += cartDetails.get(i).getQuantity() * products.get(i).getPrice();
        }

        List<CartDetailResponse> cartDetailResponses = cartDetails.stream().map(cartDetail -> {
            Product product = cartDetail.getProduct();
            ProductInOrder productInOrder = new ProductInOrder(product.getName(), product.getPrice(), product.getMainImage());
            return new CartDetailResponse(
                    cartDetail.getCartDetailId(),
                    cartDetail.getQuantity(),
                    productInOrder
            );
        }).collect(Collectors.toList());

        int shippingFee = CalculateShippingFee(subtotal);

        return new CheckOutViewModel(userResponse, cartResponse, cartDetailResponses, subtotal, shippingFee);
    }

    public ResponseData<?> cancelOrder(UUID orderId) {
        if (orderId == null) {
            throw new MasterException(HttpStatus.NOT_FOUND, "không tìm thấy đơn hàng");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "không tìm thấy đơn hàng"));

        try {
            order.setStatus("Đã hủy");
            orderRepository.save(order);
            return new ResponseData<>(HttpStatus.OK.value(), "đã hủy đơn hàng thành công");
        } catch (Exception ex) {
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred:" + ex.getMessage());
        }
    }

    public BasePage<OrderResponse> AdminIndex(ApiListBaseRequest listBaseRequest) {
        permissionCheck.checkPermission("ORDER_MANAGE");
        Page<Order> page = orderRepository.findAll(FilterDataUtil.buildPageRequest(listBaseRequest));
        return this.map(page);
    }

    public AdminEditResponse AdminCheck(UUID orderId) {
        permissionCheck.checkPermission("ORDER_MANAGE");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "không tìm thấy đơn hàng"));

        List<String> statuses = Arrays.asList(
                "Chờ xác nhận",
                "Đã xác nhận",
                "Đang đóng gói",
                "Đã bàn giao cho đơn vị vân chuyển",
                "Đang giao hàng",
                "Đã nhận hàng"
        );

        AdminOrderDetailDTO dto = orderMapper.toAdminDTO(order);
        return new AdminEditResponse(dto, statuses);    }

    public ResponseData<?> AdminEdit(UUID id, Order updatedOrder) {
        if (!id.equals(updatedOrder.getOrderId())) {
            throw new MasterException(HttpStatus.NOT_FOUND, "không tìm thấy order tương ứng");
        }

        try {
            orderRepository.save(updatedOrder);
            return new ResponseData<>(HttpStatus.OK.value(), "duyệt thành công đơn đặt hàng");
        } catch (Exception ex) {
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error: " + ex.getMessage());
        }
    }
}