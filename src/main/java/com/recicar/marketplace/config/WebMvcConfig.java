package com.recicar.marketplace.config;

import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final CategoryInterceptor categoryInterceptor;

    public WebMvcConfig(CategoryInterceptor categoryInterceptor) {
        this.categoryInterceptor = categoryInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(categoryInterceptor);
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Configure static resource handlers
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/assets/");

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("classpath:/static/fonts/");
    }
}

@Component
class CategoryInterceptor implements HandlerInterceptor {

    private final CategoryService categoryService;

    @Autowired
    public CategoryInterceptor(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        try {
            if (modelAndView != null && modelAndView.getModel() != null) {
                // Add categories to all models for navigation
                List<Category> categories = categoryService.findRootCategories();
                modelAndView.addObject("categories", categories != null ? categories : new ArrayList<>());
                System.out.println("Added " + (categories != null ? categories.size() : 0) + " categories to model");
            }
        } catch (Exception e) {
            // Log error but don't fail the request
            System.err.println("Error adding categories to model: " + e.getMessage());
            e.printStackTrace();
            if (modelAndView != null && modelAndView.getModel() != null) {
                modelAndView.addObject("categories", new ArrayList<>());
            }
        }
    }
}
