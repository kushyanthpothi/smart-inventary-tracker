package com.kushyanth.inventary.service;

import com.kushyanth.inventary.entity.InventoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendLowStockAlert(InventoryItem item) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo("warehouse@company.com"); // Configure this as needed
            message.setSubject("Low Stock Alert: " + item.getName());
            message.setText(createLowStockMessage(item));
            
            emailSender.send(message);
            logger.info("Low stock alert sent for item: {}", item.getName());
        } catch (Exception e) {
            logger.error("Failed to send low stock alert for item: {}", item.getName(), e);
        }
    }

    public void sendBulkLowStockAlert(List<InventoryItem> lowStockItems) {
        if (lowStockItems.isEmpty()) {
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo("warehouse@company.com"); // Configure this as needed
            message.setSubject("Daily Low Stock Report - " + lowStockItems.size() + " items need attention");
            message.setText(createBulkLowStockMessage(lowStockItems));
            
            emailSender.send(message);
            logger.info("Bulk low stock alert sent for {} items", lowStockItems.size());
        } catch (Exception e) {
            logger.error("Failed to send bulk low stock alert", e);
        }
    }

    private String createLowStockMessage(InventoryItem item) {
        return String.format(
            "URGENT: Low Stock Alert\n\n" +
            "Item: %s\n" +
            "SKU: %s\n" +
            "Current Quantity: %d\n" +
            "Reorder Threshold: %d\n" +
            "Supplier: %s\n" +
            "Supplier Email: %s\n" +
            "Location: %s\n\n" +
            "Please reorder this item immediately to avoid stockouts.\n\n" +
            "Inventory Management System",
            item.getName(),
            item.getSku(),
            item.getQuantity(),
            item.getReorderThreshold(),
            item.getSupplierName() != null ? item.getSupplierName() : "N/A",
            item.getSupplierEmail() != null ? item.getSupplierEmail() : "N/A",
            item.getLocation() != null ? item.getLocation() : "N/A"
        );
    }

    private String createBulkLowStockMessage(List<InventoryItem> items) {
        StringBuilder message = new StringBuilder();
        message.append("Daily Low Stock Report\n\n");
        message.append("The following items are below their reorder thresholds:\n\n");

        for (InventoryItem item : items) {
            message.append(String.format(
                "• %s (SKU: %s) - Current: %d, Threshold: %d\n",
                item.getName(),
                item.getSku(),
                item.getQuantity(),
                item.getReorderThreshold()
            ));
        }

        message.append("\nPlease review and take appropriate action.\n\n");
        message.append("Inventory Management System");

        return message.toString();
    }
}