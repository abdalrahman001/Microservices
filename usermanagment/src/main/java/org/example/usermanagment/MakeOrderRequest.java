// src/main/java/com/example/orderservice/dto/MakeOrderRequest.java
package org.example.usermanagment;

import java.util.Map;

public class MakeOrderRequest {
    private String customerId;
    private Map<String, Integer> dishesToOrder; // Map<dishId, quantity>

    // Constructors, getters, setters
    public MakeOrderRequest() {}

    public MakeOrderRequest(String customerId, Map<String, Integer> dishesToOrder) {
        this.customerId = customerId;
        this.dishesToOrder = dishesToOrder;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Map<String, Integer> getDishesToOrder() {
        return dishesToOrder;
    }

    public void setDishesToOrder(Map<String, Integer> dishesToOrder) {
        this.dishesToOrder = dishesToOrder;
    }
}