package com.kushyanth.inventary.repository;

import com.kushyanth.inventary.entity.InventoryChangeLog;
import com.kushyanth.inventary.entity.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryChangeLogRepository extends JpaRepository<InventoryChangeLog, Long> {
    
    List<InventoryChangeLog> findByInventoryItemOrderByChangeDateDesc(InventoryItem inventoryItem);
    
    Page<InventoryChangeLog> findByInventoryItemOrderByChangeDateDesc(InventoryItem inventoryItem, Pageable pageable);
    
    Page<InventoryChangeLog> findAllByOrderByChangeDateDesc(Pageable pageable);
    
    @Query("SELECT icl FROM InventoryChangeLog icl WHERE icl.changeDate BETWEEN :startDate AND :endDate ORDER BY icl.changeDate DESC")
    List<InventoryChangeLog> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT icl FROM InventoryChangeLog icl WHERE icl.changedBy = :username ORDER BY icl.changeDate DESC")
    Page<InventoryChangeLog> findByChangedBy(@Param("username") String username, Pageable pageable);
    
    @Query("SELECT icl FROM InventoryChangeLog icl WHERE icl.changeType = :changeType ORDER BY icl.changeDate DESC")
    Page<InventoryChangeLog> findByChangeType(@Param("changeType") InventoryChangeLog.ChangeType changeType, Pageable pageable);
}