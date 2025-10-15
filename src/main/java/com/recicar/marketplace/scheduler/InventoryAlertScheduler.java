package com.recicar.marketplace.scheduler;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.NotificationService;
import com.recicar.marketplace.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class InventoryAlertScheduler {

    private final ProductService productService;
    private final NotificationService notificationService;

    public InventoryAlertScheduler(ProductService productService, NotificationService notificationService) {
        this.productService = productService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 9 * * ?") // Run every day at 9 AM
    @Transactional(readOnly = true)
    public void checkLowStockProducts() {
        List<Product> lowStockProducts = productService.findLowStockProducts();
        for (Product product : lowStockProducts) {
            // In a real application, you would send a more detailed email to the vendor
            log.warn("Sending low stock alert for product: {} to vendor: {}", 
                    product.getName(), product.getVendor().getUser().getEmail());
            // notificationService.sendLowStockAlertEmail(product.getVendor().getUser(), product);
        }
    }
}
