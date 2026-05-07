package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.OrderItemRequest;
import com.recicar.marketplace.dto.OrderRequest;
import com.recicar.marketplace.dto.PaymentRequest;
import com.recicar.marketplace.dto.ShippingInfoRequest;
import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.Payment;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.OrderRepository;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

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

    @Mock
    private CartService cartService;

    @Mock
    private CartPricingService cartPricingService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest orderRequest;
    private Product product;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1L);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(100L);
        item.setQuantity(1);
        orderRequest.setItems(List.of(item));

        ShippingInfoRequest shipping = new ShippingInfoRequest();
        shipping.setAddress("Calle Alcalá 1");
        shipping.setCity("Madrid");
        shipping.setState("M");
        shipping.setZipCode("28001");
        shipping.setCountry("ES");
        orderRequest.setShippingInfo(shipping);

        PaymentRequest payment = new PaymentRequest();
        payment.setPaymentMethod("VISA");
        orderRequest.setPayment(payment);

        product = new Product();
        product.setId(100L);
        product.setName("Faro delantero");
        product.setPrice(new BigDecimal("75.00"));
        product.setStockQuantity(4);
    }

    @Test
    void createOrder_usesComputedAmountsAndSendsConfirmation() {
        User customer = new User();
        customer.setId(1L);

        CartDto pricedCart = new CartDto();
        pricedCart.setSubtotal(new BigDecimal("75.00"));

        Payment processedPayment = new Payment();
        processedPayment.setStatus(Payment.PaymentStatus.COMPLETED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(cartService.getCart(1L)).thenReturn(pricedCart);
        when(cartPricingService.computeOrderAmounts(eq(1L), eq(pricedCart), eq(orderRequest.getShippingInfo())))
                .thenReturn(new CartPricingService.OrderAmounts(
                        new BigDecimal("75.00"),
                        new BigDecimal("1.50"),
                        new BigDecimal("18.17"),
                        new BigDecimal("10.00"),
                        new BigDecimal("104.67")));
        when(paymentService.processPayment(any(Order.class), eq("VISA"))).thenReturn(processedPayment);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(notificationService).sendOrderConfirmationEmail(any(Order.class));

        Order order = orderService.createOrder(orderRequest);

        assertNotNull(order.getOrderNumber());
        assertEquals(new BigDecimal("75.00"), order.getSubtotal());
        assertEquals(new BigDecimal("1.50"), order.getServiceFee());
        assertEquals(new BigDecimal("18.17"), order.getTaxAmount());
        assertEquals(new BigDecimal("10.00"), order.getShippingAmount());
        assertEquals(new BigDecimal("104.67"), order.getTotalAmount());
        assertEquals(3, product.getStockQuantity());
        verify(notificationService).sendOrderConfirmationEmail(order);
    }

    @Test
    void createOrder_rejectsWhenCartSubtotalChanged() {
        User customer = new User();
        customer.setId(1L);

        CartDto changedCart = new CartDto();
        changedCart.setSubtotal(new BigDecimal("50.00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(cartService.getCart(1L)).thenReturn(changedCart);

        RuntimeException error = assertThrows(RuntimeException.class, () -> orderService.createOrder(orderRequest));

        assertEquals("Cart has changed; please review your order.", error.getMessage());
    }

    @Test
    void findOrdersByCustomerId_delegatesToRepository() {
        PageRequest pg = PageRequest.of(1, 10);
        Order o = new Order();
        o.setId(3L);
        when(orderRepository.findByCustomer_IdOrderByCreatedAtDesc(5L, pg)).thenReturn(List.of(o));

        List<Order> got = orderService.findOrdersByCustomerId(5L, pg);

        assertEquals(1, got.size());
        assertEquals(3L, got.get(0).getId());
        verify(orderRepository).findByCustomer_IdOrderByCreatedAtDesc(5L, pg);
    }
}
