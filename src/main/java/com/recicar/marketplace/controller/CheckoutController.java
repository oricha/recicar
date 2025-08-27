package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.CheckoutForm;
import com.recicar.marketplace.dto.OrderRequest;
import com.recicar.marketplace.dto.PaymentRequest;
import com.recicar.marketplace.dto.ShippingInfoRequest;
import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.service.CartService;
import com.recicar.marketplace.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;

    public CheckoutController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping
    public String checkoutPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // TODO: Get user ID from userDetails
        Long userId = 1L; // Assuming user ID is 1 for now
        cartService.validateCart(userId);

        CartDto cart = cartService.getCart(userId);
        model.addAttribute("cart", cart);

        BigDecimal shippingCost = cartService.calculateShippingCost(userId, "USA", "CA", "12345"); // Placeholder values
        model.addAttribute("shippingCost", shippingCost);

        model.addAttribute("checkoutForm", new CheckoutForm());

        return "checkout";
    }

    @PostMapping
    public String placeOrder(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute CheckoutForm checkoutForm) {
        // TODO: Get user ID from userDetails
        Long userId = 1L; // Assuming user ID is 1 for now
        cartService.validateCart(userId);

        CartDto cart = cartService.getCart(userId);

        ShippingInfoRequest shippingInfo = new ShippingInfoRequest();
        shippingInfo.setAddress(checkoutForm.getAddress());
        shippingInfo.setCity(checkoutForm.getCity());
        shippingInfo.setState(checkoutForm.getState());
        shippingInfo.setZipCode(checkoutForm.getZipCode());
        shippingInfo.setCountry(checkoutForm.getCountry());

        PaymentRequest payment = new PaymentRequest();
        payment.setPaymentMethod(checkoutForm.getPaymentMethod());

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(userId);
        orderRequest.setItems(orderService.convertCartItemsToOrderItems(cart.getItems()));
        orderRequest.setShippingInfo(shippingInfo);
        orderRequest.setPayment(payment);

        Order order = orderService.createOrder(orderRequest);

        // Clear the cart after successful order
        cartService.clearCart(userId);

        return "redirect:/orders/confirmation?orderNumber=" + order.getOrderNumber();
    }
}
