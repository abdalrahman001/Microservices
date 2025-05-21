package org.example.payment;

import java.math.BigDecimal;
import java.sql.Timestamp;



public class PaymentModel {
    private Long id;            // ← changed
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private BigDecimal shippingFee;
    private String status;
    private Timestamp createdAt;

    
    public PaymentModel() { }

    public PaymentModel(Long id, String orderId, String customerId,
                        BigDecimal amount, BigDecimal shippingFee,
                        String status, Timestamp createdAt) {
        this.id           = id;
        this.orderId      = orderId;
        this.customerId   = customerId;
        this.amount       = amount;
        this.shippingFee  = shippingFee;
        this.status       = status;
        this.createdAt    = createdAt;
    }

    // ─── Getters ───────────────────────────────────────────────────
    public Long getId()                  { return id; }
    public String getOrderId()           { return orderId; }
    public String getCustomerId()        { return customerId; }
    public BigDecimal getAmount()        { return amount; }
    public BigDecimal getShippingFee()   { return shippingFee; }
    public String getStatus()            { return status; }
    public Timestamp getCreatedAt()      { return createdAt; }

    // ─── Setters ───────────────────────────────────────────────────
    public void setId(Long id)                   { this.id = id; }
    public void setOrderId(String orderId)       { this.orderId = orderId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setAmount(BigDecimal amount)     { this.amount = amount; }
    public void setShippingFee(BigDecimal fee)   { this.shippingFee = fee; }
    public void setStatus(String status)         { this.status = status; }
    public void setCreatedAt(Timestamp ts)       { this.createdAt = ts; }
}
