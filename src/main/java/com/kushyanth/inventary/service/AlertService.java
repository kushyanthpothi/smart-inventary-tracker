package com.kushyanth.inventary.service;

import com.kushyanth.inventary.entity.InventoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "${inventory.alert.cron}")
    public void checkLowStockItems() {
        logger.info("Running scheduled low stock check...");
        
        try {
            List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();
            
            if (!lowStockItems.isEmpty()) {
                logger.warn("Found {} items with low stock", lowStockItems.size());
                emailService.sendBulkLowStockAlert(lowStockItems);
                
                // Log each low stock item
                for (InventoryItem item : lowStockItems) {
                    logger.warn("Low stock: {} (SKU: {}) - Current: {}, Threshold: {}", 
                               item.getName(), item.getSku(), item.getQuantity(), item.getReorderThreshold());
                }
            } else {
                logger.info("No low stock items found");
            }
        } catch (Exception e) {
            logger.error("Error during low stock check", e);
        }
    }

    // Manual method to trigger low stock check (for testing or on-demand checks)
    public List<InventoryItem> performManualLowStockCheck() {
        logger.info("Performing manual low stock check...");
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();
        
        if (!lowStockItems.isEmpty()) {
            emailService.sendBulkLowStockAlert(lowStockItems);
        }
        
        return lowStockItems;
    }
}