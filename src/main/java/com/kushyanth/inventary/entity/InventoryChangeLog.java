package com.kushyanth.inventary.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_change_logs")
public class InventoryChangeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @NotNull
    @Column(name = "old_quantity")
    private Integer oldQuantity;

    @NotNull
    @Column(name = "new_quantity")
    private Integer newQuantity;

    @NotNull
    @Column(name = "quantity_change")
    private Integer quantityChange;

    @NotBlank
    @Size(max = 50)
    @Enumerated(EnumType.STRING)
    @Column(name = "change_type")
    private ChangeType changeType;

    @Size(max = 500)
    private String reason;

    @Size(max = 100)
    @Column(name = "changed_by")
    private String changedBy;

    @Column(name = "change_date")
    private LocalDateTime changeDate;

    public InventoryChangeLog() {
        this.changeDate = LocalDateTime.now();
    }

    public InventoryChangeLog(InventoryItem inventoryItem, Integer oldQuantity, Integer newQuantity, 
                             ChangeType changeType, String reason, String changedBy) {
        this.inventoryItem = inventoryItem;
        this.oldQuantity = oldQuantity;
        this.newQuantity = newQuantity;
        this.quantityChange = newQuantity - oldQuantity;
        this.changeType = changeType;
        this.reason = reason;
        this.changedBy = changedBy;
        this.changeDate = LocalDateTime.now();
    }

    public enum ChangeType {
        STOCK_IN,
        STOCK_OUT,
        ADJUSTMENT,
        INITIAL_STOCK,
        DAMAGED,
        EXPIRED,
        SOLD,
        RETURNED
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public Integer getOldQuantity() {
        return oldQuantity;
    }

    public void setOldQuantity(Integer oldQuantity) {
        this.oldQuantity = oldQuantity;
    }

    public Integer getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(Integer newQuantity) {
        this.newQuantity = newQuantity;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }
}