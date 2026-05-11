package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.service.CategoryService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Multi-device smoke coverage for warranties-and-policies OpenSpec tasks 1.8-5.8.
 */
@WebMvcTest(controllers = LegalInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class LegalInfoMultiDeviceMockMvcTest {

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
    private CategoryService categoryService;

    @ParameterizedTest(name = "[1.8 shipping-info] {0}")
    @MethodSource("devices")
    void shippingInfoPage_viewportAndLegalNoticeStable(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/info-de-envio").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("info-de-envio"))
                .andExpect(content().string(containsString("viewport")))
                .andExpect(content().string(containsString("Versión vinculante:")));
    }

    @ParameterizedTest(name = "[2.8 payment-info] {0}")
    @MethodSource("devices")
    void paymentInfoPage_viewportAndLegalNoticeStable(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/info-de-pago").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("info-de-pago"))
                .andExpect(content().string(containsString("viewport")))
                .andExpect(content().string(containsString("Versión vinculante:")));
    }

    @ParameterizedTest(name = "[3.8 return-policy] {0}")
    @MethodSource("devices")
    void returnPolicyPage_viewportAndLegalNoticeStable(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/politica-de-devolucion").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("politica-de-devolucion"))
                .andExpect(content().string(containsString("viewport")))
                .andExpect(content().string(containsString("Versión vinculante:")));
    }

    @ParameterizedTest(name = "[4.8 terms-conditions] {0}")
    @MethodSource("devices")
    void termsPage_viewportAndLegalNoticeStable(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/terminos-de-uso").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("terminos-de-uso"))
                .andExpect(content().string(containsString("viewport")))
                .andExpect(content().string(containsString("Versión vinculante:")));
    }

    @ParameterizedTest(name = "[5.8 privacy-policy] {0}")
    @MethodSource("devices")
    void privacyPage_viewportAndLegalNoticeStable(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/politica-de-privacidad").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("politica-de-privacidad"))
                .andExpect(content().string(containsString("viewport")))
                .andExpect(content().string(containsString("Versión vinculante:")));
    }

    private static RequestPostProcessor userAgent(String agent) {
        return request -> {
            request.addHeader(HttpHeaders.USER_AGENT, agent);
            return request;
        };
    }
}
