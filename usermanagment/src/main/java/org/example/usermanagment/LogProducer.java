// src/main/java/com/example/orderservice/rabbitmq/LogProducer.java
package org.example.usermanagment;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSystemLog(String severity, String serviceName, String message) {
        String logMessage = "{\"severity\":\"" + severity + "\", \"serviceName\":\"" + serviceName + "\", \"message\":\"" + message + "\"}";
        String routingKey = "log." + severity.toLowerCase(); // e.g., log.error, log.info
        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE_LOGS, routingKey, logMessage);
        System.out.println("Sent system log to RabbitMQ: " + logMessage + " with routing key " + routingKey);
    }
}