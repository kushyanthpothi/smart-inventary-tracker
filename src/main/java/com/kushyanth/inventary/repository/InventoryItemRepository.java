package com.kushyanth.inventary.repository;

import com.kushyanth.inventary.entity.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findBySku(String sku);
    Boolean existsBySku(String sku);
    
    List<InventoryItem> findByIsActiveTrue();
    Page<InventoryItem> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= i.reorderThreshold AND i.isActive = true")
    List<InventoryItem> findLowStockItems();
    
    @Query("SELECT i FROM InventoryItem i WHERE i.isActive = true AND " +
           "(:category IS NULL OR i.category = :category) AND " +
           "(:supplierName IS NULL OR i.supplierName = :supplierName) AND " +
           "(:location IS NULL OR i.location = :location)")
    Page<InventoryItem> findByFilters(@Param("category") String category,
                                     @Param("supplierName") String supplierName,
                                     @Param("location") String location,
                                     Pageable pageable);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.isActive = true AND " +
           "(LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<InventoryItem> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT DISTINCT i.category FROM InventoryItem i WHERE i.category IS NOT NULL AND i.isActive = true")
    List<String> findDistinctCategories();
    
    @Query("SELECT DISTINCT i.supplierName FROM InventoryItem i WHERE i.supplierName IS NOT NULL AND i.isActive = true")
    List<String> findDistinctSuppliers();
    
    @Query("SELECT DISTINCT i.location FROM InventoryItem i WHERE i.location IS NOT NULL AND i.isActive = true")
    List<String> findDistinctLocations();
}