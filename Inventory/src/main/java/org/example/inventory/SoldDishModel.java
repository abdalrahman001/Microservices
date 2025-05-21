package org.example.inventory;

import java.sql.Timestamp;

public class SoldDishModel {
    private int dishId;
    private String dishName;
    private int quantity;
    private double soldPrice;
    private Timestamp orderDate;
    private String customerId;
    private String shippingCompany;

    public SoldDishModel(int dishId, String dishName, int quantity, double soldPrice,
                         Timestamp orderDate, String customerId, String shippingCompany) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.quantity = quantity;
        this.soldPrice = soldPrice;
        this.orderDate = orderDate;
        this.customerId = customerId;
        this.shippingCompany = shippingCompany;
    }

    // Getters and setters

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(double soldPrice) {
        this.soldPrice = soldPrice;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getShippingCompany() {
        return shippingCompany;
    }

    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }
}
