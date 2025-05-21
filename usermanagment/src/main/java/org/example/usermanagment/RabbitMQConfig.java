// src/main/java/com/example/orderservice/rabbitmq/RabbitMQConfig.java
package org.example.usermanagment;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchanges
    public static final String DIRECT_EXCHANGE_NOTIFICATIONS = "notifications_direct_exchange";
    public static final String TOPIC_EXCHANGE_LOGS = "log_topic_exchange";

    // Queues
    public static final String QUEUE_CUSTOMER_NOTIFICATIONS = "customer_notifications_queue";
    public static final String QUEUE_ADMIN_NOTIFICATIONS = "admin_notifications_queue";
    public static final String QUEUE_SYSTEM_LOGS = "system_logs_queue";

    // Routing Keys
    public static final String ROUTING_KEY_CUSTOMER_NO_STOCK = "customer.nostock";
    public static final String ROUTING_KEY_ADMIN_PAYMENT_FAILURE = "admin.payment.failure";
    public static final String ROUTING_KEY_LOG_INFO = "log.info";
    public static final String ROUTING_KEY_LOG_WARNING = "log.warning";
    public static final String ROUTING_KEY_LOG_ERROR = "log.error";

    @Bean
    public DirectExchange notificationsDirectExchange() {
        return new DirectExchange(DIRECT_EXCHANGE_NOTIFICATIONS);
    }

    @Bean
    public TopicExchange logTopicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_LOGS);
    }

    @Bean
    public Queue customerNotificationsQueue() {
        return new Queue(QUEUE_CUSTOMER_NOTIFICATIONS, true);
    }

    @Bean
    public Queue adminNotificationsQueue() {
        return new Queue(QUEUE_ADMIN_NOTIFICATIONS, true);
    }

    @Bean
    public Queue systemLogsQueue() {
        return new Queue(QUEUE_SYSTEM_LOGS, true);
    }

    // Bindings for Direct Exchange
    @Bean
    public Binding bindCustomerNotificationQueue() {
        return BindingBuilder.bind(customerNotificationsQueue())
                .to(notificationsDirectExchange())
                .with(ROUTING_KEY_CUSTOMER_NO_STOCK);
    }

    @Bean
    public Binding bindAdminNotificationQueue() {
        return BindingBuilder.bind(adminNotificationsQueue())
                .to(notificationsDirectExchange())
                .with(ROUTING_KEY_ADMIN_PAYMENT_FAILURE);
    }

    // Bindings for Topic Exchange (Log Exchange)
    @Bean
    public Binding bindSystemLogsQueueAllLogs() {
        return BindingBuilder.bind(systemLogsQueue())
                .to(logTopicExchange())
                .with("log.*"); // Catch all messages with "log." prefix
    }
}