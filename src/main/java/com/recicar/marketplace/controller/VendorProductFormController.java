package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.ProductRequest;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.VendorContextService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/vendor/products")
public class VendorProductFormController {

    private final VendorContextService vendorContextService;
    private final ProductService productService;
    private final CategoryService categoryService;

    public VendorProductFormController(
            VendorContextService vendorContextService,
            ProductService productService,
            CategoryService categoryService) {
        this.vendorContextService = vendorContextService;
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/new")
    public String newForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        vendorContextService.requireCurrentVendor(userDetails);
        ProductRequest r = new ProductRequest();
        r.setActive(true);
        r.setStockQuantity(0);
        model.addAttribute("productRequest", r);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("conditions", ProductCondition.values());
        model.addAttribute("pageTitle", "New product");
        model.addAttribute("isEdit", false);
        return "vendor/product-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            Model model) {
        Vendor vendor = vendorContextService.requireCurrentVendor(userDetails);
        Product product = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));
        if (!product.getVendor().getId().equals(vendor.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Not allowed to edit this product");
        }
        model.addAttribute("productRequest", toRequest(product));
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("conditions", ProductCondition.values());
        model.addAttribute("pageTitle", "Edit product");
        model.addAttribute("isEdit", true);
        return "vendor/product-form";
    }

    @PostMapping
    public String save(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute("productRequest") ProductRequest productRequest) {
        Vendor vendor = vendorContextService.requireCurrentVendor(userDetails);
        productRequest.setVendorId(vendor.getId());
        if (productRequest.getId() != null) {
            Product product = productService.findById(productRequest.getId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));
            if (!product.getVendor().getId().equals(vendor.getId())) {
                throw new ResponseStatusException(FORBIDDEN, "Not allowed to edit this product");
            }
        } else {
            productRequest.setId(null);
        }
        productService.createOrUpdateProduct(productRequest);
        return "redirect:/vendor/inventory";
    }

    private static ProductRequest toRequest(Product p) {
        ProductRequest r = new ProductRequest();
        r.setId(p.getId());
        r.setVendorId(p.getVendor().getId());
        r.setCategoryId(p.getCategory().getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setPrice(p.getPrice());
        r.setPartNumber(p.getPartNumber());
        r.setOemNumber(p.getOemNumber());
        r.setCondition(p.getCondition());
        r.setStockQuantity(p.getStockQuantity());
        r.setWeightKg(p.getWeightKg());
        r.setActive(p.isActive());
        return r;
    }
}
