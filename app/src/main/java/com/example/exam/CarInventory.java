package com.example.exam;

public class CarInventory {
    private Integer id;
    private String name ;
    private String supplier;
    private String details;
    private String status;
    private Integer quantity;
    private String type;

    public CarInventory(Integer id, String name, String supplier, String details, String status, Integer quantity, String type) {
        this.id = id;
        this.name = name;
        this.supplier = supplier;
        this.details = details;
        this.status = status;
        this.quantity = quantity;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
