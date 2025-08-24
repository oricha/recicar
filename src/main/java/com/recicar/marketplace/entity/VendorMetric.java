package com.recicar.marketplace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_metrics", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"vendor_id", "metric_date"})
})
public class VendorMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @NotNull
    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Column(name = "total_sales", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSales = BigDecimal.ZERO;

    @NotNull
    @Min(0)
    @Column(name = "order_count", nullable = false)
    private Integer orderCount = 0;

    @NotNull
    @Min(0)
    @Column(name = "product_views", nullable = false)
    private Integer productViews = 0;

    @NotNull
    @DecimalMin(value = "0.0000", inclusive = true)
    @DecimalMax(value = "1.0000", inclusive = true)
    @Column(name = "conversion_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal conversionRate = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public VendorMetric() {}

    public VendorMetric(Vendor vendor, LocalDate metricDate) {
        this.vendor = vendor;
        this.metricDate = metricDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public LocalDate getMetricDate() {
        return metricDate;
    }

    public void setMetricDate(LocalDate metricDate) {
        this.metricDate = metricDate;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public Integer getProductViews() {
        return productViews;
    }

    public void setProductViews(Integer productViews) {
        this.productViews = productViews;
    }

    public BigDecimal getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(BigDecimal conversionRate) {
        this.conversionRate = conversionRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public BigDecimal getAverageOrderValue() {
        if (orderCount == 0) {
            return BigDecimal.ZERO;
        }
        return totalSales.divide(BigDecimal.valueOf(orderCount), 2, java.math.RoundingMode.HALF_UP);
    }

    public String getFormattedTotalSales() {
        return String.format("€%.2f", totalSales);
    }

    public String getFormattedConversionRate() {
        return String.format("%.2f%%", conversionRate.multiply(BigDecimal.valueOf(100)));
    }
}