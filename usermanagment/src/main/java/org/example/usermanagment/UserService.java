package org.example.usermanagment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {

    public boolean registerAdmin(String username, String password) {
        return DatabaseManager.registerAdmin(username, password);
    }

    public boolean loginAdmin(String username, String password) {
        return DatabaseManager.loginAdmin(username, password);
    }

    public boolean addCompany(String name) {
        return DatabaseManager.addCompany(name);
    }

    public boolean updateCompany(String companyId, String newName) {
        return DatabaseManager.updateCompany(companyId, newName);
    }

    public boolean removeCompany(String companyId) {
        return DatabaseManager.removeCompany(companyId);
    }

    public boolean registerSeller(String companyId, String username, String password) {
        return DatabaseManager.registerSeller(companyId, username, password);
    }

    public boolean loginSeller(String username, String password) {
        return DatabaseManager.loginSeller(username, password);
    }

    public List<String> listSellers() {
        return DatabaseManager.listSellerAccounts();
    }

    public boolean registerCustomer(String name, String email, String password, String phone, String location) {
        return DatabaseManager.registerCustomer(name, email, password, phone, location);
    }

    public boolean loginCustomer(String email, String password) {
        return DatabaseManager.loginCustomer(email, password);
    }

    public List<String> listCustomers() {
        return DatabaseManager.listCustomers();
    }

    public boolean addAdminNotification(String orderId, String reason) {
        return DatabaseManager.addAdminNotification(orderId, reason);
    }

    public List<String> getAdminNotifications() {
        return DatabaseManager.getAdminNotifications();
    }

    public boolean addCustomerNotification(String customerId, String notification) {
        return DatabaseManager.addCustomerNotification(customerId, notification);
    }

    public List<String> getCustomerNotifications(String customerId) {
        return DatabaseManager.getCustomerNotifications(customerId);
    }

    public List<String> getAllCustomerNotifications() {
        return DatabaseManager.getAllCustomerNotifications();
    }

    public boolean addSystemLog(String severity, String serviceName, String message) {
        return DatabaseManager.addSystemLog(severity, serviceName, message);
    }

    public List<String> getSystemLogs() {
        return DatabaseManager.getSystemLogs();
    }

   
   
    @Autowired
    private NotificationProducer notificationProducer;
    @Autowired
    private LogProducer logProducer;
    @Autowired
    private ObjectMapper objectMapper; // For parsing and potentially serializing JSON responses

    @Autowired
    private RestTemplate restTemplate;

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    @Value("${order.minimum-charge}")
    private double minimumCharge;

    /**
     * Helper method to escape a string that might contain JSON characters
     * to ensure it can be safely embedded within another JSON string.
     * This uses ObjectMapper's capability to serialize a simple string,
     * which inherently handles escaping.
     */
    private String escapeJsonString(String input) {
        try {
            // ObjectMapper.writeValueAsString will wrap the string in quotes and escape internal quotes
            // e.g., "hello" becomes "\"hello\""
            // "{"key":"value"}" becomes "\"{ \\"key\\":\\"value\\\"}\""
            // We then remove the outer quotes it adds, as we want to embed this *content* within another string
            // and the calling logProducer.sendSystemLog probably expects just the content.
            String escaped = objectMapper.writeValueAsString(input);
            return escaped.substring(1, escaped.length() - 1); // Remove the outer quotes
        } catch (JsonProcessingException e) {
            System.err.println("Failed to escape string for logging: " + e.getMessage());
            return input; // Fallback to original string if escaping fails
        }
    }


    @Transactional // @Transactional here means a logical transaction from Order Service perspective
    public boolean makeOrder(String customerId, Map<String, Integer> dishesToOrder) {
        if (dishesToOrder == null || dishesToOrder.isEmpty()) {
            System.err.println("No dishes provided for the order.");
            logProducer.sendSystemLog("WARNING", "OrderService", "Attempted to make an order with no dishes for customer: " + customerId);
            return false;
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        Map<String, BigDecimal> dishPrices = new HashMap<>();

        // 1. Confirm stock availability for each dish using Inventory Service and gather prices
        for (Map.Entry<String, Integer> entry : dishesToOrder.entrySet()) {
            String dishId = entry.getKey();
            int quantity = entry.getValue();

            try {
                String checkStockUrl = inventoryServiceUrl + "/stock/check/" + dishId + "/" + quantity;
                ResponseEntity<String> response = restTemplate.getForEntity(checkStockUrl, String.class);

                if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                    JsonNode root = objectMapper.readTree(response.getBody());
                    boolean available = root.get("available").asBoolean();
                    double price = root.get("price").asDouble();

                    if (!available) {
                        System.err.println("Insufficient stock for dish ID: " + dishId);
                        notificationProducer.sendCustomerNotification(customerId, "Sorry, insufficient stock for dish: " + dishId);
                        logProducer.sendSystemLog("INFO", "OrderService", "Order failed for customer " + customerId + ": insufficient stock for dish " + dishId);
                        return false;
                    }
                    dishPrices.put(dishId, BigDecimal.valueOf(price));
                    totalPrice = totalPrice.add(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(quantity)));
                } else {
                    // This block handles non-2xx successful responses with a body, e.g., 404, 500 if they have a body
                    String errorMessage = "Failed to check stock for dish ID: " + dishId + ". Response: " + response.getStatusCodeValue();
                    if (response.hasBody()) {
                        errorMessage += ". Body: " + escapeJsonString(response.getBody()); // Safely escape body content
                    }
                    System.err.println(errorMessage);
                    notificationProducer.sendCustomerNotification(customerId, "Failed to process order. Please try again later.");
                    logProducer.sendSystemLog("ERROR", "OrderService", errorMessage);
                    return false;
                }
            } catch (HttpClientErrorException e) {
                // This catches 4xx and 5xx client errors specifically
                String errorMessage = "Inventory Service error (stock check) for dish ID " + dishId + ": " + e.getMessage();
                System.err.println(errorMessage);
                notificationProducer.sendCustomerNotification(customerId, "Failed to process order due to inventory issue. Please try again later.");
                logProducer.sendSystemLog("ERROR", "OrderService", errorMessage); // e.getMessage() can contain JSON
                return false;
            } catch (Exception e) {
                // This catches all other exceptions (e.g., network issues, JSON parsing errors)
                String errorMessage = "Error checking stock for dish ID " + dishId + ": " + e.getMessage();
                System.err.println(errorMessage);
                notificationProducer.sendCustomerNotification(customerId, "Failed to process order due to an internal error. Please try again later.");
                logProducer.sendSystemLog("ERROR", "OrderService", errorMessage); // e.getMessage() can contain JSON
                return false;
            }
        }

        // 2. Check minimum charge
        if (totalPrice.doubleValue() < minimumCharge) {
            System.err.println("Order total price " + totalPrice + " is below minimum charge " + minimumCharge);
            notificationProducer.sendCustomerNotification(customerId, "Order total price " + totalPrice + " is below minimum charge " + minimumCharge);
            logProducer.sendSystemLog("INFO", "OrderService", "Order failed for customer " + customerId + ": total price below minimum charge. Total: " + totalPrice);
            return false;
        }

        // 3. Process Payment
        String orderId = UUID.randomUUID().toString(); // Generate unique order ID for payment and subsequent inventory operations
        PaymentRequest paymentRequest = new PaymentRequest(orderId, customerId, totalPrice, BigDecimal.ZERO, BigDecimal.valueOf(minimumCharge));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentRequest> paymentEntity = new HttpEntity<>(paymentRequest, headers);

        try {
            ResponseEntity<PaymentResponse> paymentResponse = restTemplate.postForEntity(
                    paymentServiceUrl + "/process", paymentEntity, PaymentResponse.class);

            if (paymentResponse.getStatusCode().is2xxSuccessful() && paymentResponse.hasBody()) {
                if ("SUCCESS".equals(paymentResponse.getBody().getStatus())) {
                    System.out.println("Payment successful for order: " + orderId);
                    logProducer.sendSystemLog("INFO", "OrderService", "Payment successful for order: " + orderId);

                    // 4. Create Order and Order Items in Inventory Service
                    OrderCreationRequest orderCreationRequest = new OrderCreationRequest(orderId, customerId, totalPrice, dishesToOrder);
                    HttpEntity<OrderCreationRequest> orderCreationEntity = new HttpEntity<>(orderCreationRequest, headers);

                    // Assuming Inventory Service has an endpoint like /inventory/orders/create
                    // which handles saving the main Order entity and all its Order Items,
                    // and also reduces stock in a single transaction.
                    String createOrderAndItemsUrl = inventoryServiceUrl + "/orders/create";
                    ResponseEntity<String> inventoryOrderResponse = restTemplate.postForEntity(createOrderAndItemsUrl, orderCreationEntity, String.class);

                    if (!inventoryOrderResponse.getStatusCode().is2xxSuccessful()) {
                        String errorMessage = "Failed to create order and order items in Inventory Service for order " + orderId + ". Status: " + inventoryOrderResponse.getStatusCodeValue();
                        if (inventoryOrderResponse.hasBody()) {
                            errorMessage += ". Body: " + escapeJsonString(inventoryOrderResponse.getBody());
                        }
                        System.err.println(errorMessage);
                        // This is a critical failure. You might need to trigger a payment rollback
                        // or manual intervention for reconciliation.
                        logProducer.sendSystemLog("ERROR", "OrderService", errorMessage);
                        notificationProducer.sendAdminNotification(orderId, "Failed to persist order/items in Inventory Service after successful payment.");
                        notificationProducer.sendCustomerNotification(customerId, "Order failed due to an internal processing error. Please contact support.");
                        // Implement compensation logic here (e.g., call Payment Service to refund)
                        return false;
                    }
                    logProducer.sendSystemLog("INFO", "OrderService", "Order and items successfully created in Inventory Service for order: " + orderId);
                    notificationProducer.sendCustomerNotification(customerId, "Your order #" + orderId + " has been placed successfully!");
                    return true;

                } else {
                    // Payment FAILED (status in response body is not "SUCCESS")
                    String paymentFailureMessage = "Payment failed for order " + orderId + ". Reason: " + paymentResponse.getBody().getMessage();
                    System.err.println(paymentFailureMessage);
                    notificationProducer.sendAdminNotification(orderId, "Payment failure due to: " + paymentResponse.getBody().getMessage());
                    logProducer.sendSystemLog("ERROR", "OrderService", paymentFailureMessage);
                    notificationProducer.sendCustomerNotification(customerId, "Payment failed for your order. Reason: " + paymentResponse.getBody().getMessage());
                    return false;
                }
            } else {
                // Payment service returned a non-2xx successful status or no body
                String unexpectedPaymentResponse = "Payment service returned an unexpected response for order: " + orderId + ". Status: " + paymentResponse.getStatusCodeValue();
                if (paymentResponse.hasBody()) {
                    try {
                        String bodyJson = objectMapper.writeValueAsString(paymentResponse.getBody());
                        unexpectedPaymentResponse += ". Body: " + escapeJsonString(bodyJson);
                    } catch (JsonProcessingException ex) {
                        unexpectedPaymentResponse += ". Body: [Failed to serialize PaymentResponse: " + ex.getMessage() + "]";
                    }
                }
                System.err.println(unexpectedPaymentResponse);
                notificationProducer.sendAdminNotification(orderId, "Payment service returned an unexpected response. Status: " + paymentResponse.getStatusCodeValue());
                logProducer.sendSystemLog("ERROR", "OrderService", unexpectedPaymentResponse);
                notificationProducer.sendCustomerNotification(customerId, "Failed to process payment due to an unexpected error. Please try again later.");
                return false;
            }
        } catch (HttpClientErrorException e) {
            // Catch specific 4xx/5xx errors from Payment Service
            String errorMessage = "Payment Service error for order " + orderId + ": " + e.getMessage();
            System.err.println(errorMessage);
            notificationProducer.sendAdminNotification(orderId, "Payment service error: " + e.getMessage());
            logProducer.sendSystemLog("ERROR", "OrderService", errorMessage); // e.getMessage() can contain JSON
            notificationProducer.sendCustomerNotification(customerId, "Failed to process payment due to an internal error. Please try again later.");
            return false;
        } catch (Exception e) {
            // Catch general exceptions during payment processing (e.g., network issues)
            String errorMessage = "Error processing payment for order " + orderId + ": " + e.getMessage();
            System.err.println(errorMessage);
            notificationProducer.sendAdminNotification(orderId, "Error processing payment: " + e.getMessage());
            logProducer.sendSystemLog("ERROR", "OrderService", errorMessage); // e.getMessage() can contain JSON
            notificationProducer.sendCustomerNotification(customerId, "Failed to process payment due to an internal error. Please try again later.");
            return false;
        }
    }
}
