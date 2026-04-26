package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.ProductCardDto;
import com.recicar.marketplace.dto.ProductDetailDto;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.ProductImage;
import com.recicar.marketplace.entity.VehicleInfo;
import com.recicar.marketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            dto.setRelatedParts(getRelatedParts(productId));
            return dto;
        });
    }

    public List<String> getProductImages(Long productId) {
        return productRepository.findById(productId)
                .map(this::getProductImages)
                .orElse(List.of());
    }

    public List<ProductCardDto> getRelatedParts(Long productId) {
        return productService.findRelatedProducts(productId).stream()
                .map(this::toProductCard)
                .collect(Collectors.toList());
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

    private ProductCardDto toProductCard(Product product) {
        ProductCardDto cardDto = new ProductCardDto();
        cardDto.setId(product.getId());
        cardDto.setName(product.getName());
        cardDto.setPartNumber(product.getPartNumber());
        cardDto.setPrice(product.getPrice());
        cardDto.setCondition(product.getCondition() != null ? product.getCondition().name() : null);
        cardDto.setInStock(product.isInStock());
        cardDto.setImageUrl(product.getPrimaryImage() != null ? product.getPrimaryImage().getImageUrl() : null);
        cardDto.setSellerName(product.getVendor() != null ? product.getVendor().getBusinessName() : null);
        return cardDto;
    }

    private String buildTitle(Product product) {
        String make = product.getVehicleInfo() != null ? emptyOrValue(product.getVehicleInfo().getMake()) : "";
        String model = product.getVehicleInfo() != null ? emptyOrValue(product.getVehicleInfo().getModel()) : "";
        String generation = product.getVehicleInfo() != null ? emptyOrValue(product.getVehicleInfo().getGeneration()) : "";
        return (make + " " + model + " " + generation + " - " + product.getName()).trim().replaceAll("\\s+", " ");
    }

    private String emptyOrValue(String value) {
        return value == null ? "" : value;
    }
}
