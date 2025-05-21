// This would be in your User Management Service
// src/main/java/com/example/usermanagementservice/rabbitmq/NotificationConsumer.java
package org.example.usermanagment;

// import com.example.usermanagementservice.service.UserService; // Removed invalid import
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @Autowired
    private UserService userService; // Assuming your UserService is injected here
    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "customer_notifications_queue")
    public void handleCustomerNotification(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String customerId = jsonNode.get("customerId").asText();
            String notification = jsonNode.get("notification").asText();
            System.out.println("Received customer notification: " + notification + " for customer " + customerId);
            userService.addCustomerNotification(customerId, notification);
        } catch (Exception e) {
            System.err.println("Error processing customer notification: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "admin_notifications_queue")
    public void handleAdminNotification(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String orderId = jsonNode.get("orderId").asText();
            String reason = jsonNode.get("reason").asText();
            System.out.println("Received admin notification: Payment failure for order " + orderId + ", Reason: " + reason);
            userService.addAdminNotification(orderId, reason);
        } catch (Exception e) {
            System.err.println("Error processing admin notification: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "system_logs_queue")
    public void handleSystemLog(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String severity = jsonNode.get("severity").asText();
            String serviceName = jsonNode.get("serviceName").asText();
            String logMessage = jsonNode.get("message").asText();
            System.out.println("Received system log: Severity=" + severity + ", Service=" + serviceName + ", Message=" + logMessage);
            userService.addSystemLog(severity, serviceName, logMessage);
        } catch (Exception e) {
            System.err.println("Error processing system log: " + e.getMessage());
        }
    }
}