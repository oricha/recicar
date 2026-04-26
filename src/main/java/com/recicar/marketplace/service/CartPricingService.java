package com.recicar.marketplace.service;

import com.recicar.marketplace.config.CheckoutProperties;
import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.ShippingInfoRequest;
import com.recicar.marketplace.entity.Cart;
import com.recicar.marketplace.entity.CartItem;
import com.recicar.marketplace.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Subtotal, marketplace service fee, VAT and shipping; total = subtotal + fee + shipping + VAT.
 */
@Service
public class CartPricingService {

    private final CheckoutProperties checkoutProperties;
    private final CartRepository cartRepository;
    private final DpdShippingService dpdShippingService;

    public CartPricingService(
            CheckoutProperties checkoutProperties, CartRepository cartRepository, DpdShippingService dpdShippingService) {
        this.checkoutProperties = checkoutProperties;
        this.cartRepository = cartRepository;
        this.dpdShippingService = dpdShippingService;
    }

    public void applyPricing(CartDto cart, Long userId, String country, String state, String zipCode) {
        Objects.requireNonNull(cart, "cart");
        if (cart.getSubtotal() == null) {
            cart.setSubtotal(BigDecimal.ZERO);
        }
        BigDecimal subtotal = cart.getSubtotal();
        BigDecimal serviceFee = subtotal.multiply(checkoutProperties.getServiceFeeRate())
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal shipping = BigDecimal.ZERO;
        if (userId != null && country != null && !country.isBlank()) {
            BigDecimal base = calculateShippingForCart(userId, country);
            base = base.add(dpdShippingService.dpdSurcharge(country));
            shipping = base;
        }
        BigDecimal vat = subtotal.add(serviceFee).add(shipping)
                .multiply(checkoutProperties.getVatRate())
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(serviceFee).add(shipping).add(vat);

        cart.setServiceFee(serviceFee);
        cart.setVatRate(checkoutProperties.getVatRate());
        cart.setVatAmount(vat);
        cart.setShippingAmount(shipping);
        cart.setTotalAmount(total);
        cart.setShippingCarrierLabel(dpdShippingService.isEuDestination(country) ? dpdShippingService.getBrandLabel() : null);
    }

    /**
     * Server-side line totals for persisting the order (must match applyPricing for same inputs).
     */
    public OrderAmounts computeOrderAmounts(Long userId, CartDto cart, ShippingInfoRequest ship) {
        applyPricing(cart, userId, ship.getCountry(), ship.getState(), ship.getZipCode());
        return new OrderAmounts(
                cart.getSubtotal(),
                cart.getServiceFee(),
                cart.getVatAmount(),
                cart.getShippingAmount(),
                cart.getTotalAmount()
        );
    }

    public record OrderAmounts(
            BigDecimal subtotal,
            BigDecimal serviceFee,
            BigDecimal vatAmount,
            BigDecimal shippingAmount,
            BigDecimal totalAmount) {
    }

    /**
     * Same rules as {@link CartService#calculateShippingCost} (flat rate by cart subtotal).
     */
    private BigDecimal calculateShippingForCart(Long userId, String country) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) {
            return BigDecimal.valueOf(10.00);
        }
        BigDecimal subtotal = cart.getItems().stream()
                .map((CartItem item) -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (subtotal.compareTo(BigDecimal.valueOf(100)) < 0) {
            return BigDecimal.valueOf(10.00);
        }
        return BigDecimal.ZERO;
    }
}
