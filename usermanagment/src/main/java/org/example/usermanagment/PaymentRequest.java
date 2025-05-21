// src/main/java/com/example/orderservice/dto/PaymentRequest.java
package org.example.usermanagment;
import java.math.BigDecimal;

public class PaymentRequest {
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private BigDecimal shippingFee;
    private BigDecimal minimumCharge;

    // Constructors, getters, setters
    public PaymentRequest() {}

    public PaymentRequest(String orderId, String customerId, BigDecimal amount, BigDecimal shippingFee, BigDecimal minimumCharge) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.shippingFee = shippingFee;
        this.minimumCharge = minimumCharge;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getMinimumCharge() {
        return minimumCharge;
    }

    public void setMinimumCharge(BigDecimal minimumCharge) {
        this.minimumCharge = minimumCharge;
    }
}