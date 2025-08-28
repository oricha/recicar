package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.OrderItemRequest;
import com.recicar.marketplace.dto.OrderRequest;
import com.recicar.marketplace.dto.ShippingInfoRequest;
import com.recicar.marketplace.dto.PaymentRequest;
import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.Payment;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.OrderRepository;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.PaymentService;
import com.recicar.marketplace.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrder() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1L);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(1);
        orderRequest.setItems(Collections.singletonList(itemRequest));

        ShippingInfoRequest shippingInfoRequest = new ShippingInfoRequest();
        shippingInfoRequest.setAddress("123 Main St");
        shippingInfoRequest.setCity("Anytown");
        shippingInfoRequest.setState("CA");
        shippingInfoRequest.setZipCode("12345");
        shippingInfoRequest.setCountry("USA");
        orderRequest.setShippingInfo(shippingInfoRequest);

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPaymentMethod("Credit Card");
        orderRequest.setPayment(paymentRequest);

        User customer = new User();
        customer.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        
        // Mock payment and notification services
        Payment mockPayment = new Payment();
        mockPayment.setStatus(Payment.PaymentStatus.COMPLETED);
        when(paymentService.processPayment(any(Order.class), anyString())).thenReturn(mockPayment);
        doNothing().when(notificationService).sendOrderConfirmationEmail(any(Order.class));

        Order order = orderService.createOrder(orderRequest);

        assertNotNull(order);
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("10.00"), order.getSubtotal());
        assertEquals(9, product.getStockQuantity());
    }
}
