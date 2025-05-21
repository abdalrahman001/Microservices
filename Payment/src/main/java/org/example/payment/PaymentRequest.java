package org.example.payment;

import java.math.BigDecimal;

public class PaymentRequest {
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private BigDecimal shippingFee;
    private BigDecimal minimumCharge;

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public BigDecimal getMinimumCharge() {
        return minimumCharge;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public void setMinimumCharge(BigDecimal minimumCharge) {
        this.minimumCharge = minimumCharge;
    }
}


