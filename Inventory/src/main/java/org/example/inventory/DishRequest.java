package org.example.inventory;

public class DishRequest {
    private String name;
    private String seller_id;
    private String description;
    private double price;
    private int stock;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSellerId() { return seller_id; }
    public void setSellerId(String seller_id) { this.seller_id = seller_id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
