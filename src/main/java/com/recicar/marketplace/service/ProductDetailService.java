package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.ProductCardDto;
import com.recicar.marketplace.dto.ProductDetailDto;
import com.recicar.marketplace.dto.CategoryBreadcrumbDto;
import com.recicar.marketplace.dto.VehicleCompatibilityDto;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.ProductImage;
import com.recicar.marketplace.entity.VehicleCompatibility;
import com.recicar.marketplace.entity.VehicleInfo;
import com.recicar.marketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductDetailService {

    private final ProductRepository productRepository;
    private final ProductService productService;

    public ProductDetailService(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    public Optional<ProductDetailDto> getProductDetail(Long productId) {
        return productRepository.findById(productId).map(product -> {
            ProductDetailDto dto = new ProductDetailDto();
            dto.setId(product.getId());
            dto.setSku(product.getPartNumber() != null ? product.getPartNumber() : "SKU-" + product.getId());
            dto.setTitle(buildTitle(product));
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setCondition(product.getCondition() != null ? product.getCondition().name() : "UNKNOWN");
            dto.setStockQuantity(product.getStockQuantity());
            dto.setSellerName(product.getVendor() != null ? product.getVendor().getBusinessName() : "N/A");
            dto.setSellerContact(product.getVendor() != null ? product.getVendor().getContactEmail() : null);
            dto.setProductImageUrls(getProductImages(product));
            dto.setProductSpecs(getProductSpecs(product));
            dto.setVehicleSpecs(getVehicleSpecs(product.getVehicleInfo()));
            dto.setVehiclePhotoUrls(getVehiclePhotos(product.getVehicleInfo()));
            dto.setRelatedParts(productService.mapListToProductCardPage(
                    productService.findRelatedProducts(productId)).getContent());
            dto.setCategoryBreadcrumb(buildCategoryBreadcrumb(product.getCategory()));
            dto.setCompatibleVehicles(buildCompatibleVehicles(product));
            ProductCardDto selfCard = productService.mapListToProductCardPage(Collections.singletonList(product))
                    .getContent()
                    .stream()
                    .findFirst()
                    .orElse(null);
            if (selfCard != null) {
                dto.setSellerRating(selfCard.getSellerRating());
                dto.setSellerTopSeller(selfCard.isTopSeller());
                dto.setServiceFeePercent(selfCard.getServiceFeePercent());
                dto.setServiceFeeMin(selfCard.getServiceFeeMin());
                dto.setServiceFeeMax(selfCard.getServiceFeeMax());
            }
            return dto;
        });
    }

    public List<String> getProductImages(Long productId) {
        return productRepository.findById(productId)
                .map(this::getProductImages)
                .orElse(List.of());
    }

    public List<ProductCardDto> getRelatedParts(Long productId) {
        return productService.mapListToProductCardPage(productService.findRelatedProducts(productId))
                .getContent();
    }

    private List<VehicleCompatibilityDto> buildCompatibleVehicles(Product product) {
        if (product.getCompatibilities() == null || product.getCompatibilities().isEmpty()) {
            return List.of();
        }
        return product.getCompatibilities().stream()
                .sorted(Comparator.comparing(VehicleCompatibility::getMake, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(VehicleCompatibility::getModel, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(ProductDetailService::toCompatibilityDto)
                .collect(Collectors.toList());
    }

    private static VehicleCompatibilityDto toCompatibilityDto(VehicleCompatibility v) {
        VehicleCompatibilityDto d = new VehicleCompatibilityDto();
        d.setMake(v.getMake());
        d.setModel(v.getModel());
        d.setYearFrom(v.getYearFrom());
        d.setYearTo(v.getYearTo());
        d.setEngine(v.getEngine());
        return d;
    }

    private List<CategoryBreadcrumbDto> buildCategoryBreadcrumb(Category category) {
        if (category == null) {
            return List.of();
        }
        List<CategoryBreadcrumbDto> trail = new ArrayList<>();
        Category c = category;
        while (c != null) {
            trail.add(0, new CategoryBreadcrumbDto(c.getName(), c.getSlug()));
            c = c.getParent();
        }
        return trail;
    }

    private String buildTitle(Product product) {
        String make = product.getVehicleInfo() != null ? emptyOrValue(product.getVehicleInfo().getMake()) : "";
        String model = product.getVehicleInfo() != null ? emptyOrValue(product.getVehicleInfo().getModel()) : "";
        String generation = product.getVehicleInfo() != null ? emptyOrValue(product.getVehicleInfo().getGeneration()) : "";
        return (make + " " + model + " " + generation + " - " + product.getName()).trim().replaceAll("\\s+", " ");
    }

    private List<String> getProductImages(Product product) {
        return product.getImages().stream()
                .sorted(Comparator.comparing(ProductImage::getSortOrder))
                .limit(7)
                .map(ProductImage::getImageUrl)
                .toList();
    }

    private Map<String, String> getProductSpecs(Product product) {
        Map<String, String> specs = new LinkedHashMap<>();
        specs.put("manufacturerCode", emptyOrValue(product.getPartNumber()));
        specs.put("oemCode", emptyOrValue(product.getOemNumber()));
        specs.put("condition", product.getCondition() != null ? product.getCondition().name() : "");
        specs.put("quality", product.isInStock() ? "IN_STOCK" : "OUT_OF_STOCK");
        specs.put("position", "N/A");
        specs.put("category", product.getCategory() != null ? emptyOrValue(product.getCategory().getName()) : "");
        specs.put("weightKg", product.getWeightKg() != null ? product.getWeightKg().toString() : "");
        specs.put("availableStock", product.getStockQuantity() != null ? product.getStockQuantity().toString() : "0");
        return specs;
    }

    private Map<String, String> getVehicleSpecs(VehicleInfo vehicleInfo) {
        Map<String, String> specs = new LinkedHashMap<>();
        if (vehicleInfo == null) {
            return specs;
        }
        specs.put("make", emptyOrValue(vehicleInfo.getMake()));
        specs.put("model", emptyOrValue(vehicleInfo.getModel()));
        specs.put("generation", emptyOrValue(vehicleInfo.getGeneration()));
        specs.put("year", vehicleInfo.getProductionYear() != null ? vehicleInfo.getProductionYear().toString() : "");
        specs.put("engineDisplacementCc", vehicleInfo.getEngineDisplacementCc() != null ? vehicleInfo.getEngineDisplacementCc().toString() : "");
        specs.put("enginePowerKw", vehicleInfo.getEnginePowerKw() != null ? vehicleInfo.getEnginePowerKw().toString() : "");
        specs.put("transmission", emptyOrValue(vehicleInfo.getTransmission()));
        specs.put("drivetrain", emptyOrValue(vehicleInfo.getDrivetrain()));
        specs.put("fuelType", emptyOrValue(vehicleInfo.getFuelType()));
        specs.put("bodyType", emptyOrValue(vehicleInfo.getBodyType()));
        specs.put("color", emptyOrValue(vehicleInfo.getColor()));
        specs.put("vin", emptyOrValue(vehicleInfo.getVin()));
        specs.put("mileageKm", vehicleInfo.getMileageKm() != null ? vehicleInfo.getMileageKm().toString() : "");
        specs.put("doors", vehicleInfo.getDoors() != null ? vehicleInfo.getDoors().toString() : "");
        return specs;
    }

    private List<String> getVehiclePhotos(VehicleInfo vehicleInfo) {
        if (vehicleInfo == null || vehicleInfo.getPhotoUrls() == null || vehicleInfo.getPhotoUrls().isBlank()) {
            return List.of();
        }
        return List.of(vehicleInfo.getPhotoUrls().split(",")).stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .limit(15)
                .toList();
    }

    private String emptyOrValue(String value) {
        return value == null ? "" : value;
    }
}
