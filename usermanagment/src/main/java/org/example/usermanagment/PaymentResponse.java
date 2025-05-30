// src/main/java/com/example/orderservice/dto/PaymentResponse.java
package org.example.usermanagment;

public class PaymentResponse {
    private String status; // Assuming "SUCCESS" or "FAILED"
    private String message;

    // Constructors, getters, setters
    public PaymentResponse() {}

    public PaymentResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}