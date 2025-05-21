// src/main/java/com/example/orderservice/rabbitmq/NotificationProducer.java
package org.example.usermanagment;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendCustomerNotification(String customerId, String notification) {
        // Here we'll just send the message content as a String, the consumer will parse it.
        // For a more robust solution, you'd send a JSON payload.
        String message = "{\"customerId\":\"" + customerId + "\", \"notification\":\"" + notification + "\"}";
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE_NOTIFICATIONS, RabbitMQConfig.ROUTING_KEY_CUSTOMER_NO_STOCK, message);
        System.out.println("Sent customer notification to RabbitMQ: " + message);
    }

    public void sendAdminNotification(String orderId, String reason) {
        String message = "{\"orderId\":\"" + orderId + "\", \"reason\":\"" + reason + "\"}";
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE_NOTIFICATIONS, RabbitMQConfig.ROUTING_KEY_ADMIN_PAYMENT_FAILURE, message);
        System.out.println("Sent admin notification to RabbitMQ: " + message);
    }
}