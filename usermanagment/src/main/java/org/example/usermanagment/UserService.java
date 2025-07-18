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
    private ObjectMapper objectMapper; // For parsing and potentially serializing JSON responses

    @Autowired
    private RestTemplate restTemplate;

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    @Value("${order.minimum-charge}")
    private double minimumCharge;

  }