package com.kushyanth.inventary.service;

import com.kushyanth.inventary.dto.InventoryItemRequest;
import com.kushyanth.inventary.dto.StockUpdateRequest;
import com.kushyanth.inventary.entity.InventoryChangeLog;
import com.kushyanth.inventary.entity.InventoryItem;
import com.kushyanth.inventary.repository.InventoryChangeLogRepository;
import com.kushyanth.inventary.repository.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryService {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private InventoryChangeLogRepository changeLogRepository;

    @Autowired
    private EmailService emailService;

    public Page<InventoryItem> getAllItems(Pageable pageable) {
        return inventoryItemRepository.findByIsActiveTrue(pageable);
    }

    public Optional<InventoryItem> getItemById(Long id) {
        return inventoryItemRepository.findById(id)
                .filter(InventoryItem::getIsActive);
    }

    public Optional<InventoryItem> getItemBySku(String sku) {
        return inventoryItemRepository.findBySku(sku);
    }

    public InventoryItem createItem(InventoryItemRequest request) {
        if (inventoryItemRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Item with SKU " + request.getSku() + " already exists");
        }

        InventoryItem item = new InventoryItem();
        updateItemFromRequest(item, request);
        item.setCreatedBy(getCurrentUsername());
        item.setUpdatedBy(getCurrentUsername());

        InventoryItem savedItem = inventoryItemRepository.save(item);

        // Log the initial stock
        logInventoryChange(savedItem, 0, savedItem.getQuantity(), 
                          InventoryChangeLog.ChangeType.INITIAL_STOCK, 
                          "Initial stock entry", getCurrentUsername());

        return savedItem;
    }

    public InventoryItem updateItem(Long id, InventoryItemRequest request) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .filter(InventoryItem::getIsActive)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        updateItemFromRequest(item, request);
        item.setUpdatedBy(getCurrentUsername());

        return inventoryItemRepository.save(item);
    }

    public InventoryItem updateStock(Long id, StockUpdateRequest request) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .filter(InventoryItem::getIsActive)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        Integer oldQuantity = item.getQuantity();
        item.setQuantity(request.getNewQuantity());
        item.setUpdatedBy(getCurrentUsername());

        InventoryItem updatedItem = inventoryItemRepository.save(item);

        // Log the change
        logInventoryChange(updatedItem, oldQuantity, request.getNewQuantity(),
                          request.getChangeType(), request.getReason(), getCurrentUsername());

        // Check if item is now low stock
        if (updatedItem.isLowStock()) {
            emailService.sendLowStockAlert(updatedItem);
        }

        return updatedItem;
    }

    public void deleteItem(Long id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .filter(InventoryItem::getIsActive)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        item.setIsActive(false);
        item.setUpdatedBy(getCurrentUsername());
        inventoryItemRepository.save(item);
    }

    public List<InventoryItem> getLowStockItems() {
        return inventoryItemRepository.findLowStockItems();
    }

    public Page<InventoryItem> searchItems(String searchTerm, Pageable pageable) {
        return inventoryItemRepository.findBySearchTerm(searchTerm, pageable);
    }

    public Page<InventoryItem> filterItems(String category, String supplierName, String location, Pageable pageable) {
        return inventoryItemRepository.findByFilters(category, supplierName, location, pageable);
    }

    public List<String> getDistinctCategories() {
        return inventoryItemRepository.findDistinctCategories();
    }

    public List<String> getDistinctSuppliers() {
        return inventoryItemRepository.findDistinctSuppliers();
    }

    public List<String> getDistinctLocations() {
        return inventoryItemRepository.findDistinctLocations();
    }

    public Page<InventoryChangeLog> getItemHistory(Long itemId, Pageable pageable) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
        
        return changeLogRepository.findByInventoryItemOrderByChangeDateDesc(item, pageable);
    }

    public Page<InventoryChangeLog> getAllChangeLogs(Pageable pageable) {
        return changeLogRepository.findAllByOrderByChangeDateDesc(pageable);
    }

    public List<InventoryChangeLog> getChangeLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return changeLogRepository.findByDateRange(startDate, endDate);
    }

    private void updateItemFromRequest(InventoryItem item, InventoryItemRequest request) {
        item.setName(request.getName());
        item.setSku(request.getSku());
        item.setDescription(request.getDescription());
        item.setQuantity(request.getQuantity());
        item.setReorderThreshold(request.getReorderThreshold());
        item.setUnitPrice(request.getUnitPrice());
        item.setCategory(request.getCategory());
        item.setSupplierName(request.getSupplierName());
        item.setSupplierEmail(request.getSupplierEmail());
        item.setSupplierPhone(request.getSupplierPhone());
        item.setLocation(request.getLocation());
    }

    private void logInventoryChange(InventoryItem item, Integer oldQuantity, Integer newQuantity,
                                   InventoryChangeLog.ChangeType changeType, String reason, String changedBy) {
        InventoryChangeLog changeLog = new InventoryChangeLog(item, oldQuantity, newQuantity, 
                                                              changeType, reason, changedBy);
        changeLogRepository.save(changeLog);
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}