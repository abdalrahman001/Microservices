// src/main/java/com/example/orderservice/dto/OrderCreationRequest.java
package org.example.usermanagment;

import java.math.BigDecimal;
import java.util.Map;

public class OrderCreationRequest {
    private String orderId;
    private String customerId;
    private BigDecimal totalPrice;
    private Map<String, Integer> dishesToOrder; // Include items to create OrderItems

    public OrderCreationRequest() {}

    public OrderCreationRequest(String orderId, String customerId, BigDecimal totalPrice, Map<String, Integer> dishesToOrder) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalPrice = totalPrice;
        this.dishesToOrder = dishesToOrder;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Map<String, Integer> getDishesToOrder() {
        return dishesToOrder;
    }

    public void setDishesToOrder(Map<String, Integer> dishesToOrder) {
        this.dishesToOrder = dishesToOrder;
    }
}