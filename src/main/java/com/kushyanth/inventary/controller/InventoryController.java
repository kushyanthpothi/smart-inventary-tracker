package com.kushyanth.inventary.controller;

import com.kushyanth.inventary.dto.InventoryItemRequest;
import com.kushyanth.inventary.dto.StockUpdateRequest;
import com.kushyanth.inventary.entity.InventoryChangeLog;
import com.kushyanth.inventary.entity.InventoryItem;
import com.kushyanth.inventary.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/items")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Page<InventoryItem>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InventoryItem> items = inventoryService.getAllItems(pageable);
        
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<InventoryItem> getItemById(@PathVariable Long id) {
        return inventoryService.getItemById(id)
                .map(item -> ResponseEntity.ok().body(item))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/items/sku/{sku}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<InventoryItem> getItemBySku(@PathVariable String sku) {
        return inventoryService.getItemBySku(sku)
                .map(item -> ResponseEntity.ok().body(item))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<InventoryItem> createItem(@Valid @RequestBody InventoryItemRequest request) {
        try {
            InventoryItem createdItem = inventoryService.createItem(request);
            return ResponseEntity.ok(createdItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<InventoryItem> updateItem(@PathVariable Long id, 
                                                   @Valid @RequestBody InventoryItemRequest request) {
        try {
            InventoryItem updatedItem = inventoryService.updateItem(id, request);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/items/{id}/stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<InventoryItem> updateStock(@PathVariable Long id,
                                                    @Valid @RequestBody StockUpdateRequest request) {
        try {
            InventoryItem updatedItem = inventoryService.updateStock(id, request);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        try {
            inventoryService.deleteItem(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/items/low-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<List<InventoryItem>> getLowStockItems() {
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();
        return ResponseEntity.ok(lowStockItems);
    }

    @GetMapping("/items/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Page<InventoryItem>> searchItems(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryItem> items = inventoryService.searchItems(searchTerm, pageable);
        
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/filter")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Page<InventoryItem>> filterItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryItem> items = inventoryService.filterItems(category, supplier, location, pageable);
        
        return ResponseEntity.ok(items);
    }

    @GetMapping("/metadata/categories")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = inventoryService.getDistinctCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/metadata/suppliers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<List<String>> getSuppliers() {
        List<String> suppliers = inventoryService.getDistinctSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/metadata/locations")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<List<String>> getLocations() {
        List<String> locations = inventoryService.getDistinctLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/items/{id}/history")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Page<InventoryChangeLog>> getItemHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryChangeLog> history = inventoryService.getItemHistory(id, pageable);
        
        return ResponseEntity.ok(history);
    }

    @GetMapping("/changes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Page<InventoryChangeLog>> getAllChangeLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryChangeLog> changes = inventoryService.getAllChangeLogs(pageable);
        
        return ResponseEntity.ok(changes);
    }
}