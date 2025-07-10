package com.kushyanth.inventary.controller;

import com.kushyanth.inventary.entity.InventoryItem;
import com.kushyanth.inventary.service.AlertService;
import com.kushyanth.inventary.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private AlertService alertService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Get all active items
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<InventoryItem> allItems = inventoryService.getAllItems(pageable);
        
        // Calculate basic stats
        List<InventoryItem> items = allItems.getContent();
        int totalItems = items.size();
        long totalQuantity = items.stream().mapToLong(InventoryItem::getQuantity).sum();
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();
        int lowStockCount = lowStockItems.size();
        
        // Calculate category distribution
        Map<String, Long> categoryDistribution = new HashMap<>();
        items.stream()
             .filter(item -> item.getCategory() != null)
             .forEach(item -> categoryDistribution.merge(item.getCategory(), 1L, Long::sum));
        
        // Calculate supplier distribution
        Map<String, Long> supplierDistribution = new HashMap<>();
        items.stream()
             .filter(item -> item.getSupplierName() != null)
             .forEach(item -> supplierDistribution.merge(item.getSupplierName(), 1L, Long::sum));
        
        // Calculate location distribution
        Map<String, Long> locationDistribution = new HashMap<>();
        items.stream()
             .filter(item -> item.getLocation() != null)
             .forEach(item -> locationDistribution.merge(item.getLocation(), 1L, Long::sum));
        
        // Calculate stock status distribution
        long healthyStockCount = items.stream()
                .filter(item -> !item.isLowStock())
                .count();
        
        stats.put("totalItems", totalItems);
        stats.put("totalQuantity", totalQuantity);
        stats.put("lowStockCount", lowStockCount);
        stats.put("healthyStockCount", healthyStockCount);
        stats.put("categoryDistribution", categoryDistribution);
        stats.put("supplierDistribution", supplierDistribution);
        stats.put("locationDistribution", locationDistribution);
        
        // Stock status for charts
        Map<String, Long> stockStatus = new HashMap<>();
        stockStatus.put("healthy", healthyStockCount);
        stockStatus.put("lowStock", (long) lowStockCount);
        stats.put("stockStatus", stockStatus);
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/low-stock-items")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<List<InventoryItem>> getLowStockItems() {
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();
        return ResponseEntity.ok(lowStockItems);
    }

    @GetMapping("/top-categories")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Map<String, Long>> getTopCategories(@RequestParam(defaultValue = "5") int limit) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<InventoryItem> allItems = inventoryService.getAllItems(pageable);
        
        Map<String, Long> categoryCount = new HashMap<>();
        allItems.getContent().stream()
                .filter(item -> item.getCategory() != null)
                .forEach(item -> categoryCount.merge(item.getCategory(), 1L, Long::sum));
        
        // Sort and limit
        Map<String, Long> topCategories = categoryCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(HashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        HashMap::putAll);
        
        return ResponseEntity.ok(topCategories);
    }

    @PostMapping("/check-alerts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Map<String, Object>> triggerManualAlertCheck() {
        List<InventoryItem> lowStockItems = alertService.performManualLowStockCheck();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Manual alert check completed");
        response.put("lowStockItemsCount", lowStockItems.size());
        response.put("lowStockItems", lowStockItems);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent-activity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Object> getRecentActivity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(inventoryService.getAllChangeLogs(pageable));
    }
}