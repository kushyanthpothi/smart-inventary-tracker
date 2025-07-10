package com.kushyanth.inventary.dto;

import com.kushyanth.inventary.entity.InventoryChangeLog;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StockUpdateRequest {
    @NotNull
    private Integer newQuantity;

    @NotNull
    private InventoryChangeLog.ChangeType changeType;

    @Size(max = 500)
    private String reason;

    public Integer getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(Integer newQuantity) {
        this.newQuantity = newQuantity;
    }

    public InventoryChangeLog.ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(InventoryChangeLog.ChangeType changeType) {
        this.changeType = changeType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}