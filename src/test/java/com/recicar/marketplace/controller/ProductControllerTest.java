package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.ProductDetailDto;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductDetailService;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.web.ProductDetailSeoHelper;
import com.recicar.marketplace.web.ShopListingModelHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductDetailService productDetailService;

    @Mock
    private ProductDetailSeoHelper productDetailSeoHelper;

    private ShopListingModelHelper shopListingModelHelper;

    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        when(productService.mapToProductCardPage(any(Page.class))).thenAnswer(invocation -> {
            Page<Product> p = invocation.getArgument(0);
            return new PageImpl<>(Collections.emptyList(), p.getPageable(), p.getTotalElements());
        });
        shopListingModelHelper = new ShopListingModelHelper(productService);
        productController = new ProductController(productService, categoryService, shopListingModelHelper,
                productDetailService, productDetailSeoHelper);

        when(productDetailSeoHelper.resolvePublicOrigin(any())).thenReturn("http://localhost");
        when(productDetailSeoHelper.metaDescription(any())).thenReturn("desc");
        when(productDetailSeoHelper.productJsonLd(any(ProductDetailDto.class), anyString())).thenReturn("{}");
        when(productDetailSeoHelper.breadcrumbJsonLd(any(), anyString(), anyString(), anyString()))
                .thenReturn("{}");

        org.springframework.web.servlet.view.InternalResourceViewResolver viewResolver =
                new org.springframework.web.servlet.view.InternalResourceViewResolver();
        viewResolver.setPrefix("");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void productList_shouldReturnShopListWithProductsAndCategories() throws Exception {
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.TEN);
        products.add(product);

        Page<Product> productPage = new PageImpl<>(products);

        List<Category> categories = new ArrayList<>();
        Category category = new Category("Test Category", "test-category");
        category.setId(1L);
        categories.add(category);

        when(productService.findActiveProducts(anyInt(), anyInt())).thenReturn(productPage);
        when(categoryService.findAllActive()).thenReturn(categories);

        mockMvc.perform(get("/shop-list"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("categories", categories));
    }

    @Test
    void productDetails_shouldReturnProductDetails_whenProductExists() throws Exception {
        ProductDetailDto dto = new ProductDetailDto();
        dto.setId(1L);
        dto.setTitle("Test Product");
        dto.setPrice(BigDecimal.TEN);

        when(productDetailService.getProductDetail(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/product-details").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-details"))
                .andExpect(model().attributeExists("detail"))
                .andExpect(model().attribute("detail", dto));
    }

    @Test
    void productDetails_shouldReturnErrorPage_whenProductDoesNotExist() throws Exception {
        when(productDetailService.getProductDetail(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/product-details").param("id", "1"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));
    }
}
