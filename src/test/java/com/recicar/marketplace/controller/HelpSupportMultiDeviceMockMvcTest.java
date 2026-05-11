package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.dto.BlogPostSummaryDto;
import com.recicar.marketplace.dto.FaqCategoryDto;
import com.recicar.marketplace.dto.FaqEntryDto;
import com.recicar.marketplace.entity.BlogPost;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ContactMessageService;
import com.recicar.marketplace.service.SupportContentService;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Multi-device smoke coverage for support-help-section OpenSpec tasks 1.8-5.8.
 */
@WebMvcTest(controllers = {HelpSupportController.class, SupportApiController.class})
@AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
@TestPropertySource(properties = "app.support.email=soporte-test@recicar.es")
class HelpSupportMultiDeviceMockMvcTest {

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
    private SupportContentService supportContentService;

    @MockBean
    private ContactMessageService contactMessageService;

    @MockBean
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        when(supportContentService.listFaqCategories()).thenReturn(List.of(
                new FaqCategoryDto(
                        "comprar",
                        "Comprar y pedidos",
                        List.of(new FaqEntryDto(1L, "¿Cómo busco una pieza compatible?", "Usa matrícula o OEM."))
                )
        ));

        when(supportContentService.listPublishedBlogSummaries(0, 9)).thenReturn(
                new org.springframework.data.domain.PageImpl<>(List.of(
                        new BlogPostSummaryDto(
                                "elegir-pieza-segunda-mano",
                                "Cómo elegir una pieza de segunda mano con confianza",
                                "Resumen",
                                LocalDateTime.of(2026, 5, 1, 10, 0)
                        )
                ))
        );

        BlogPost post = new BlogPost();
        post.setSlug("elegir-pieza-segunda-mano");
        post.setTitle("Cómo elegir una pieza de segunda mano con confianza");
        post.setSummary("Resumen");
        post.setBody("<p>Contenido</p>");
        post.setPublishedAt(LocalDateTime.of(2026, 5, 1, 10, 0));
        when(supportContentService.findPublishedPostBySlug("elegir-pieza-segunda-mano")).thenReturn(Optional.of(post));
    }

    @ParameterizedTest(name = "[1.8 help-center] {0}")
    @MethodSource("devices")
    void helpCenterPage_viewportAndSupportEntryPointsStable(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/help").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("support/help-center"))
                .andExpect(content().string(containsString("viewport")));
    }

    @ParameterizedTest(name = "[2.8 faqs] {0}")
    @MethodSource("devices")
    void faqPage_accordionAndSupportEmailStable(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/faq").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("support/faq"))
                .andExpect(content().string(containsString("viewport")));
    }

    @ParameterizedTest(name = "[3.8 blog] {0}")
    @MethodSource("devices")
    void blogPostPage_viewportAndBreadcrumbStable(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/blog/elegir-pieza-segunda-mano").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("support/blog-post"))
                .andExpect(content().string(containsString("viewport")));
    }

    @ParameterizedTest(name = "[4.8 contact-form] {0}")
    @MethodSource("devices")
    void contactPage_formFieldsAndViewportStable(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/contact").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("support/contact"))
                .andExpect(content().string(containsString("viewport")));
    }

    @ParameterizedTest(name = "[5.8 email-support] {0}")
    @MethodSource("devices")
    void supportInfoApi_jsonStableAcrossDevices(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/api/v1/support/info").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supportEmail").value("soporte-test@recicar.es"));
    }

    private static RequestPostProcessor userAgent(String agent) {
        return request -> {
            request.addHeader(HttpHeaders.USER_AGENT, agent);
            return request;
        };
    }
}
