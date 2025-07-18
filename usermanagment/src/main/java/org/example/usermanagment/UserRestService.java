package org.example.usermanagment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.example.usermanagment.UserService;

@RestController
@RequestMapping("/api")
public class UserRestService {

    private final UserService userService;

    @Autowired
    public UserRestService(UserService userService) {
        this.userService = userService;
    }

    // Admin


    @GetMapping(value = "/seller/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> listSellers() {
        return userService.listSellers();
    }

    @GetMapping(value = "/customer/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> listCustomers() {
        return userService.listCustomers();
    }

    // Admin Notifications

    @GetMapping(value = "/admin/notification/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getAdminNotifications() {
        return userService.getAdminNotifications();
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

 
    // Get all system logs
    @GetMapping(value = "/system/logs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getSystemLogs() {
        return userService.getSystemLogs();
    }




}
