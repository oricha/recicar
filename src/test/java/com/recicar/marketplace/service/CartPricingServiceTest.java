package com.recicar.marketplace.service;

import com.recicar.marketplace.config.CheckoutProperties;
import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.ShippingInfoRequest;
import com.recicar.marketplace.entity.Cart;
import com.recicar.marketplace.entity.CartItem;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartPricingServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private DpdShippingService dpdShippingService;

    private CartPricingService cartPricingService;

    @BeforeEach
    void setUp() {
        CheckoutProperties properties = new CheckoutProperties();
        properties.setServiceFeeRate(new BigDecimal("0.02"));
        properties.setVatRate(new BigDecimal("0.21"));
        cartPricingService = new CartPricingService(properties, cartRepository, dpdShippingService);
    }

    @Test
    void applyPricing_appliesFeeVatShippingAndCarrierLabel() {
        CartDto cart = new CartDto();
        cart.setSubtotal(new BigDecimal("80.00"));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cartEntityWithSnapshotPrice("80.00")));
        when(dpdShippingService.dpdSurcharge("ES")).thenReturn(new BigDecimal("2.50"));
        when(dpdShippingService.isEuDestination("ES")).thenReturn(true);
        when(dpdShippingService.getBrandLabel()).thenReturn("DPD (envío EU)");

        cartPricingService.applyPricing(cart, 1L, "ES", "M", "28001");

        assertEquals(new BigDecimal("1.60"), cart.getServiceFee());
        assertEquals(new BigDecimal("12.50"), cart.getShippingAmount());
        assertEquals(new BigDecimal("19.76"), cart.getVatAmount());
        assertEquals(new BigDecimal("113.86"), cart.getTotalAmount());
        assertEquals("DPD (envío EU)", cart.getShippingCarrierLabel());
    }

    @Test
    void computeOrderAmounts_usesCartItemSnapshotPriceForShippingThreshold() {
        CartDto cart = new CartDto();
        cart.setSubtotal(new BigDecimal("40.00"));

        ShippingInfoRequest shipping = new ShippingInfoRequest();
        shipping.setCountry("ES");
        shipping.setState("M");
        shipping.setZipCode("28001");

        Cart entity = cartEntityWithSnapshotPrice("40.00");
        entity.getItems().get(0).getProduct().setPrice(new BigDecimal("99.00"));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(entity));
        when(dpdShippingService.dpdSurcharge("ES")).thenReturn(BigDecimal.ZERO);
        when(dpdShippingService.isEuDestination("ES")).thenReturn(true);
        when(dpdShippingService.getBrandLabel()).thenReturn("DPD");

        CartPricingService.OrderAmounts amounts = cartPricingService.computeOrderAmounts(1L, cart, shipping);

        assertEquals(0, amounts.subtotal().compareTo(new BigDecimal("40.00")));
        assertEquals(0, amounts.serviceFee().compareTo(new BigDecimal("0.80")));
        assertEquals(0, amounts.shippingAmount().compareTo(new BigDecimal("10.00")));
        assertEquals(0, amounts.vatAmount().compareTo(new BigDecimal("10.67")));
        assertEquals(0, amounts.totalAmount().compareTo(new BigDecimal("61.47")));
    }

    private static Cart cartEntityWithSnapshotPrice(String snapshotPrice) {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(new BigDecimal(snapshotPrice));

        CartItem item = new CartItem();
        item.setId(11L);
        item.setProduct(product);
        item.setQuantity(1);
        item.setPrice(new BigDecimal(snapshotPrice));

        Cart cart = new Cart();
        cart.setId(5L);
        cart.setItems(List.of(item));
        item.setCart(cart);
        return cart;
    }
}
