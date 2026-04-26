package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.CheckoutForm;
import com.recicar.marketplace.dto.OrderRequest;
import com.recicar.marketplace.dto.PaymentRequest;
import com.recicar.marketplace.dto.ShippingInfoRequest;
import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.PaymentMethodOption;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.CartPricingService;
import com.recicar.marketplace.service.CartService;
import com.recicar.marketplace.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutFlowController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final CartPricingService cartPricingService;

    public CheckoutFlowController(
            CartService cartService,
            OrderService orderService,
            UserRepository userRepository,
            CartPricingService cartPricingService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.cartPricingService = cartPricingService;
    }

    @GetMapping
    public String root() {
        return "redirect:/checkout/shipping";
    }

    @GetMapping("/shipping")
    public String shipping(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            HttpSession session) {
        User user = requireUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }
        cartService.validateCart(user.getId());
        model.addAttribute("step", 1);
        model.addAttribute("cart", cartService.getCart(user.getId()));
        model.addAttribute("checkoutForm", CheckoutSessionSupport.getShippingDraft(session));
        return "checkout-shipping";
    }

    @PostMapping("/shipping")
    public String postShipping(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute CheckoutForm form,
            HttpSession session) {
        User user = requireUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }
        cartService.validateCart(user.getId());
        CheckoutSessionSupport.setShippingDraft(session, form);
        return "redirect:/checkout/payment";
    }

    @GetMapping("/payment")
    public String payment(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            HttpSession session) {
        User user = requireUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }
        CheckoutForm ship = CheckoutSessionSupport.getShippingDraft(session);
        if (isBlank(ship.getAddress()) || isBlank(ship.getCountry())) {
            return "redirect:/checkout/shipping";
        }
        cartService.validateCart(user.getId());
        CartDto cart = cartService.getCart(user.getId());
        cartPricingService.applyPricing(
                cart, user.getId(), ship.getCountry(), ship.getState(), ship.getZipCode());
        model.addAttribute("step", 2);
        model.addAttribute("cart", cart);
        model.addAttribute("shippingDraft", ship);
        model.addAttribute("paymentMethods", PaymentMethodOption.values());
        model.addAttribute("paymentMethod", ship.getPaymentMethod() != null ? ship.getPaymentMethod() : "VISA");
        return "checkout-payment";
    }

    @PostMapping("/confirm")
    public String confirm(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String paymentMethod,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = requireUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }
        CheckoutForm ship = CheckoutSessionSupport.getShippingDraft(session);
        if (isBlank(ship.getAddress()) || isBlank(ship.getCountry())) {
            return "redirect:/checkout/shipping";
        }
        ship.setPaymentMethod(paymentMethod);
        cartService.validateCart(user.getId());
        CartDto cart = cartService.getCart(user.getId());

        ShippingInfoRequest shippingInfo = new ShippingInfoRequest();
        shippingInfo.setAddress(ship.getAddress());
        shippingInfo.setCity(ship.getCity());
        shippingInfo.setState(ship.getState());
        shippingInfo.setZipCode(ship.getZipCode());
        shippingInfo.setCountry(ship.getCountry());

        PaymentRequest payment = new PaymentRequest();
        payment.setPaymentMethod(paymentMethod);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(user.getId());
        orderRequest.setItems(orderService.convertCartItemsToOrderItems(cart.getItems()));
        orderRequest.setShippingInfo(shippingInfo);
        orderRequest.setPayment(payment);

        try {
            Order order = orderService.createOrder(orderRequest);
            cartService.clearCart(user.getId());
            CheckoutSessionSupport.clear(session);
            return "redirect:/orders/confirmation?orderNumber=" + order.getOrderNumber();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("checkoutError", e.getMessage());
            return "redirect:/checkout/payment";
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private User requireUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userRepository.findByEmailIgnoreCase(userDetails.getUsername()).orElse(null);
    }
}
