package org.example.inventory;

import java.sql.Timestamp;

public class DishModel {
    private int id;
    private String seller_id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private Timestamp createdAt;

    // ✅ No-argument constructor
    public DishModel() {
    }

    // ✅ Parameterized constructor
    public DishModel(int id, String seller_id, String name, String description, double price, int stock, Timestamp createdAt) {
        this.id = id;
        this.seller_id = seller_id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
