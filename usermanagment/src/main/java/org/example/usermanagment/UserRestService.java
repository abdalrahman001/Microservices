package org.example.usermanagment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.example.usermanagment.UserService;
import org.example.usermanagment.EmailLoginRequest;

@RestController
@RequestMapping("/api")
public class UserRestService {

    private final UserService userService;

    @Autowired
    public UserRestService(UserService userService) {
        this.userService = userService;
    }

    // Admin
    @PostMapping(value = "/admin/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean registerAdmin(@RequestBody CredentialRequest req) {
        return userService.registerAdmin(req.getUsername(), req.getPassword());
    }

    @PostMapping(value = "/admin/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean loginAdmin(@RequestBody CredentialRequest req) {
        return userService.loginAdmin(req.getUsername(), req.getPassword());
    }

    // Company
    @PostMapping(value = "/company/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean addCompany(@RequestBody NameRequest req) {
        return userService.addCompany(req.getName());
    }

    @PostMapping(value = "/company/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateCompany(@RequestBody UpdateCompanyRequest req) {
        return userService.updateCompany(req.getId(), req.getNewName());
    }

    @PostMapping(value = "/company/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean removeCompany(@RequestBody IdRequest req) {
        return userService.removeCompany(req.getId());
    }

    // Seller
    @PostMapping(value = "/seller/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean registerSeller(@RequestBody SellerRegisterRequest req) {
        return userService.registerSeller(req.getCompanyId(), req.getUsername(), req.getPassword());
    }

    @PostMapping(value = "/seller/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean loginSeller(@RequestBody CredentialRequest req) {
        return userService.loginSeller(req.getUsername(), req.getPassword());
    }

    @GetMapping(value = "/seller/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> listSellers() {
        return userService.listSellers();
    }

    // Customer
    @PostMapping(value = "/customer/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean registerCustomer(@RequestBody CustomerRegisterRequest req) {
        return userService.registerCustomer(req.getName(), req.getEmail(), req.getPassword(), req.getPhone(), req.getLocation());
    }

    @PostMapping(value = "/customer/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean loginCustomer(@RequestBody EmailLoginRequest req) {
        return userService.loginCustomer(req.getEmail(), req.getPassword());
    }

    @GetMapping(value = "/customer/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> listCustomers() {
        return userService.listCustomers();
    }

    // Admin Notifications
    @PostMapping(value = "/admin/notification/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean addAdminNotification(@RequestBody AdminNotificationRequest req) {
        return userService.addAdminNotification(req.getOrderId(), req.getReason());
    }

    @GetMapping(value = "/admin/notification/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getAdminNotifications() {
        return userService.getAdminNotifications();
    }

    // Add a customer notification
    @PostMapping(value = "/customer/notification/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean addCustomerNotification(@RequestBody CustomerNotificationRequest req) {
        return userService.addCustomerNotification(req.getCustomerId(), req.getNotification());
    }

    // Get notifications for a specific customer
    @GetMapping(value = "/customer/notification/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getCustomerNotifications(@PathVariable String customerId) {
        return userService.getCustomerNotifications(customerId);
    }

    // Get all customer notifications
    @GetMapping(value = "/customer/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getAllCustomerNotifications() {
        return userService.getAllCustomerNotifications();
    }

    // Add a system log
    @PostMapping(value = "/system/log/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean addSystemLog(@RequestBody SystemLogRequest req) {
        return userService.addSystemLog(req.getSeverity(), req.getServiceName(), req.getMessage());
    }

    // Get all system logs
    @GetMapping(value = "/system/logs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getSystemLogs() {
        return userService.getSystemLogs();
    }

    @PostMapping(value = "/orders/make", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> makeOrder(@RequestBody MakeOrderRequest req) {
        boolean success = userService.makeOrder(req.getCustomerId(), req.getDishesToOrder());
        if (success) {
            return ResponseEntity.ok("Order placed successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to place order.");
        }
    }



}
