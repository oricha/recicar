package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.CheckoutForm;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.CartPricingService;
import com.recicar.marketplace.service.CartService;
import com.recicar.marketplace.service.OrderService;
import com.recicar.marketplace.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Smoke coverage for OpenSpec shopping-cart-checkout tasks 1.8, 2.8, 3.8, 4.8 and 5.8.
 * Verifies cart + checkout markup remains viewport-ready and stable across common client user agents.
 */
@WebMvcTest(controllers = {CartController.class, CheckoutFlowController.class})
@AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class ShoppingCartCheckoutMultiDeviceMockMvcTest {

    private static final String UA_MOBILE = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) "
            + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1";
    private static final String UA_TABLET = "Mozilla/5.0 (iPad; CPU OS 17_0 like Mac OS X) "
            + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1";
    private static final String UA_DESKTOP = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    static Stream<Arguments> devices() {
        return Stream.of(
                Arguments.of("mobile", UA_MOBILE),
                Arguments.of("tablet", UA_TABLET),
                Arguments.of("desktop", UA_DESKTOP)
        );
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CartPricingService cartPricingService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setEmail("buyer@example.com");
        when(userRepository.findByEmailIgnoreCase("buyer@example.com")).thenReturn(java.util.Optional.of(user));

        CartDto cart = new CartDto();
        cart.setUserId(1L);
        cart.setSubtotal(new BigDecimal("80.00"));
        cart.setServiceFee(new BigDecimal("1.60"));
        cart.setShippingAmount(new BigDecimal("10.00"));
        cart.setVatAmount(new BigDecimal("19.24"));
        cart.setTotalAmount(new BigDecimal("110.84"));
        cart.setShippingCarrierLabel("DPD (envío EU)");

        when(cartService.getCart(anyLong())).thenReturn(cart);
        doNothing().when(cartService).validateCart(anyLong());
    }

    @ParameterizedTest(name = "[1.8 persistent-cart] {0}")
    @MethodSource("devices")
    @WithMockUser(username = "buyer@example.com", roles = "CUSTOMER")
    void cartPage_isViewportReady(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/cart").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(content().string(containsString("viewport")))
                .andExpect(content().string(containsString("Proceder al pago")));
    }

    @ParameterizedTest(name = "[2.8 price-calculation] {0}")
    @MethodSource("devices")
    @WithMockUser(username = "buyer@example.com", roles = "CUSTOMER")
    void paymentPage_displaysPriceBreakdown(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/checkout/payment").session(checkoutSession()).with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout-payment"))
                .andExpect(content().string(containsString("Tarifa de servicio")))
                .andExpect(content().string(containsString("IVA")))
                .andExpect(content().string(containsString("Total")));
    }

    @ParameterizedTest(name = "[3.8 multi-step-checkout] {0}")
    @MethodSource("devices")
    @WithMockUser(username = "buyer@example.com", roles = "CUSTOMER")
    void shippingPage_showsStepOne(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/checkout/shipping").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout-shipping"))
                .andExpect(content().string(containsString("Paso 1 de 2")));
    }

    @ParameterizedTest(name = "[4.8 payment-methods] {0}")
    @MethodSource("devices")
    @WithMockUser(username = "buyer@example.com", roles = "CUSTOMER")
    void paymentPage_listsAvailableMethods(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/checkout/payment").session(checkoutSession()).with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("VISA")))
                .andExpect(content().string(containsString("PAYPAL")))
                .andExpect(content().string(containsString("BANK_TRANSFER")));
    }

    @ParameterizedTest(name = "[5.8 shipping-integration] {0}")
    @MethodSource("devices")
    @WithMockUser(username = "buyer@example.com", roles = "CUSTOMER")
    void paymentPage_showsDpdShippingContext(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/checkout/payment").session(checkoutSession()).with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("DPD (envío EU)")));
    }

    private static MockHttpSession checkoutSession() {
        MockHttpSession session = new MockHttpSession();
        CheckoutForm form = new CheckoutForm();
        form.setAddress("Calle Atocha 10");
        form.setCity("Madrid");
        form.setState("M");
        form.setZipCode("28012");
        form.setCountry("ES");
        session.setAttribute(CheckoutSessionSupport.SHIPPING, form);
        return session;
    }

    private static RequestPostProcessor userAgent(String agent) {
        return request -> {
            request.addHeader(HttpHeaders.USER_AGENT, agent);
            return request;
        };
    }
}
