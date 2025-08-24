package com.recicar.marketplace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_compatibility")
public class VehicleCompatibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String make;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String model;

    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(name = "year_from", nullable = false)
    private Integer yearFrom;

    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(name = "year_to", nullable = false)
    private Integer yearTo;

    @Size(max = 100)
    private String engine;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public VehicleCompatibility() {}

    public VehicleCompatibility(Product product, String make, String model, Integer yearFrom, Integer yearTo) {
        this.product = product;
        this.make = make;
        this.model = model;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(Integer yearFrom) {
        this.yearFrom = yearFrom;
    }

    public Integer getYearTo() {
        return yearTo;
    }

    public void setYearTo(Integer yearTo) {
        this.yearTo = yearTo;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public String getYearRange() {
        if (yearFrom.equals(yearTo)) {
            return yearFrom.toString();
        }
        return yearFrom + "-" + yearTo;
    }

    public String getVehicleDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(make).append(" ").append(model);
        sb.append(" (").append(getYearRange()).append(")");
        if (engine != null && !engine.trim().isEmpty()) {
            sb.append(" - ").append(engine);
        }
        return sb.toString();
    }

    public boolean isCompatibleWithYear(Integer year) {
        return year != null && year >= yearFrom && year <= yearTo;
    }
}