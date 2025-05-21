package org.example.usermanagment;

public class CustomerNotificationRequest {
    private String customerId;
    private String notification;

    public CustomerNotificationRequest() {
    }

    public CustomerNotificationRequest(String customerId, String notification) {
        this.customerId = customerId;
        this.notification = notification;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
