package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.CheckoutForm;
import com.recicar.marketplace.entity.PaymentMethodOption;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.CartPricingService;
import com.recicar.marketplace.service.CartService;
import com.recicar.marketplace.service.OrderService;
import com.recicar.marketplace.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = CheckoutFlowController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class CheckoutFlowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartPricingService cartPricingService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setEmail("checkout@example.com");
        when(userRepository.findByEmailIgnoreCase("checkout@example.com")).thenReturn(Optional.of(user));

        CartDto cart = new CartDto();
        cart.setSubtotal(new BigDecimal("50.00"));
        cart.setServiceFee(new BigDecimal("1.00"));
        cart.setVatAmount(new BigDecimal("12.81"));
        cart.setShippingAmount(new BigDecimal("10.00"));
        cart.setTotalAmount(new BigDecimal("73.81"));
        cart.setShippingCarrierLabel("DPD (envío EU)");
        when(cartService.getCart(anyLong())).thenReturn(cart);
        doNothing().when(cartService).validateCart(anyLong());
    }

    @Test
    @WithMockUser(username = "checkout@example.com", roles = "CUSTOMER")
    void paymentRedirectsToShippingWhenDraftIsMissing() throws Exception {
        mockMvc.perform(get("/checkout/payment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/shipping"));
    }

    @Test
    @WithMockUser(username = "checkout@example.com", roles = "CUSTOMER")
    void paymentPageShowsAvailablePaymentMethods() throws Exception {
        MockHttpSession session = new MockHttpSession();
        CheckoutForm form = new CheckoutForm();
        form.setAddress("Calle Mayor 1");
        form.setCity("Madrid");
        form.setCountry("ES");
        form.setZipCode("28001");
        session.setAttribute(CheckoutSessionSupport.SHIPPING, form);

        mockMvc.perform(get("/checkout/payment").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout-payment"))
                .andExpect(model().attributeExists("shippingDraft"))
                .andExpect(model().attributeExists("cart"))
                .andExpect(content().string(containsString(PaymentMethodOption.VISA.name())))
                .andExpect(content().string(containsString(PaymentMethodOption.PAYPAL.name())));
    }

    @Test
    @WithMockUser(username = "checkout@example.com", roles = "CUSTOMER")
    void confirmRedirectsBackToShippingWhenDraftIsMissing() throws Exception {
        mockMvc.perform(post("/checkout/confirm").param("paymentMethod", "VISA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/shipping"));
    }
}
