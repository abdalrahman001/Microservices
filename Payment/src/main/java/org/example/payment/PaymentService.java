package org.example.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<PaymentModel> mapper = (rs, rowNum) -> new PaymentModel(
            rs.getLong("id"),                  // ← getLong
            rs.getString("order_id"),
            rs.getString("customer_id"),
            rs.getBigDecimal("amount"),
            rs.getBigDecimal("shipping_fee"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
    );

    /** Process a new payment */
    public PaymentModel processPayment(String orderId,
                                       String customerId,
                                       BigDecimal amount,
                                       BigDecimal shippingFee,
                                       BigDecimal minimumCharge) {
        String status = amount.add(shippingFee).compareTo(minimumCharge) >= 0
                ? "SUCCESS" : "FAILED";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO payments (order_id, customer_id, amount, shipping_fee, status) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, orderId);
            ps.setString(2, customerId);
            ps.setBigDecimal(3, amount);
            ps.setBigDecimal(4, shippingFee);
            ps.setString(5, status);
            return ps;
        }, keyHolder);

        // Retrieve the generated key
        Number key = keyHolder.getKey();
        Long newId = (key != null) ? key.longValue() : null;
        return getPaymentById(newId);
    }

    /** Rollback (mark as FAILED) an existing payment */
    public PaymentModel rollbackPayment(Long paymentId, String reason) {
        jdbc.update("UPDATE payments SET status = 'FAILED' WHERE id = ?", paymentId);
        return getPaymentById(paymentId);
    }

    public List<PaymentModel> getPaymentsByCustomer(String customerId) {
        return jdbc.query(
                "SELECT * FROM payments WHERE customer_id = ?",
                mapper, customerId
        );
    }

    public PaymentModel getPaymentByOrder(String orderId) {
        return jdbc.queryForObject(
                "SELECT * FROM payments WHERE order_id = ?",
                mapper, orderId
        );
    }

    public PaymentModel getPaymentById(Long paymentId) {
        return jdbc.queryForObject(
                "SELECT * FROM payments WHERE id = ?",
                mapper, paymentId
        );
    }

    public List<PaymentModel> getFailedPayments() {
        return jdbc.query(
                "SELECT * FROM payments WHERE status = 'FAILED'",
                mapper
        );
    }
}
