package org.example.payment;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService service;

    @PostMapping("/process")
public ResponseEntity<?> process(@RequestBody PaymentRequest request) {
    try {
        PaymentModel payment = service.processPayment(
            request.getOrderId(),
            request.getCustomerId(),
            request.getAmount(),
            request.getShippingFee(),
            request.getMinimumCharge()
        );
        return ResponseEntity.ok(payment);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error processing payment: " + e.getMessage());
    }
}

    /** 2. Rollback payment */
    @PostMapping("/rollback")
    public ResponseEntity<PaymentModel> rollback(@RequestBody Map<String, String> body) {
        String paymentId = body.get("paymentId");
        String reason    = body.get("reason");
        PaymentModel p   = service.rollbackPayment(Long.valueOf(paymentId), reason);
        return ResponseEntity.ok(p);
    }

    /** 3. Get payments by customer */
    @GetMapping("/customer/{customerId}")
    public List<PaymentModel> byCustomer(@PathVariable String customerId) {
        return service.getPaymentsByCustomer(customerId);
    }

    /** 4. Get payment by order */
    @GetMapping("/order/{orderId}")
    public PaymentModel byOrder(@PathVariable String orderId) {
        return service.getPaymentByOrder(orderId);
    }

    /** 5. Get payment by ID */
    @GetMapping("/{paymentId}")
    public PaymentModel byId(@PathVariable String paymentId) {
        return service.getPaymentById(Long.valueOf(paymentId));
    }

    /** 6. Admin: list failed payments */
    @GetMapping("/failures")
    public List<PaymentModel> failures() {
        return service.getFailedPayments();
    }
}
