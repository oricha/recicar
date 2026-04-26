package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.OrderItemRequest;
import com.recicar.marketplace.dto.OrderRequest;
import com.recicar.marketplace.entity.*;
import com.recicar.marketplace.repository.OrderRepository;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final CartService cartService;
    private final CartPricingService cartPricingService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            PaymentService paymentService,
            NotificationService notificationService,
            CartService cartService,
            CartPricingService cartPricingService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
        this.cartService = cartService;
        this.cartPricingService = cartPricingService;
    }

    @Override
    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(Order.OrderStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        User customer = userRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        order.setCustomer(customer);

        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(itemRequest -> createOrderItem(itemRequest, order))
                .collect(Collectors.toList());
        order.setItems(orderItems);

        BigDecimal lineSubtotal = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartDto pricedCart = cartService.getCart(orderRequest.getCustomerId());
        if (pricedCart.getSubtotal().compareTo(lineSubtotal) != 0) {
            throw new RuntimeException("Cart has changed; please review your order.");
        }
        CartPricingService.OrderAmounts amounts = cartPricingService.computeOrderAmounts(
                orderRequest.getCustomerId(), pricedCart, orderRequest.getShippingInfo());
        order.setSubtotal(amounts.subtotal());
        order.setServiceFee(amounts.serviceFee());
        order.setTaxAmount(amounts.vatAmount());
        order.setShippingAmount(amounts.shippingAmount());
        order.setTotalAmount(amounts.totalAmount());

        ShippingInfo shippingInfo = new ShippingInfo();
        shippingInfo.setAddressLine1(orderRequest.getShippingInfo().getAddress());
        shippingInfo.setCity(orderRequest.getShippingInfo().getCity());
        shippingInfo.setState(orderRequest.getShippingInfo().getState());
        shippingInfo.setPostalCode(orderRequest.getShippingInfo().getZipCode());
        shippingInfo.setCountry(orderRequest.getShippingInfo().getCountry());
        shippingInfo.setCreatedAt(LocalDateTime.now());
        order.setShippingInfo(shippingInfo);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(orderRequest.getPayment().getPaymentMethod());
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        order.setPayment(payment);

        // Process payment
        paymentService.processPayment(order, orderRequest.getPayment().getPaymentMethod());

        Order savedOrder = orderRepository.save(order);

        // Send order confirmation email
        notificationService.sendOrderConfirmationEmail(savedOrder);

        return savedOrder;
    }

    private OrderItem createOrderItem(OrderItemRequest itemRequest, Order order) {
        Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < itemRequest.getQuantity()) {
            throw new RuntimeException("Not enough stock for product: " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
        productRepository.save(product);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(itemRequest.getQuantity());
        orderItem.setUnitPrice(product.getPrice());
        return orderItem;
    }

    private String generateOrderNumber() {
        return UUID.randomUUID().toString();
    }

    @Override
    public List<OrderItemRequest> convertCartItemsToOrderItems(List<com.recicar.marketplace.dto.CartItemDto> cartItems) {
        return cartItems.stream()
                .map(cartItemDto -> {
                    OrderItemRequest orderItemRequest = new OrderItemRequest();
                    orderItemRequest.setProductId(cartItemDto.getProductId());
                    orderItemRequest.setQuantity(cartItemDto.getQuantity());
                    return orderItemRequest;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findOrdersByCustomerId(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomer_IdOrderByCreatedAtDesc(customerId, pageable);
    }
}
