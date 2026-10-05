package com.monika.recharge.repository;

import com.monika.recharge.dto.MobileRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class MobileRepository {
    private final JdbcTemplate jdbc;

    public MobileRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findAll(String keyword) {
        String q = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        return jdbc.queryForList("""
            SELECT m.mobile_id AS mobileId,
                   m.mobile_number AS mobileNumber,
                   m.operator_name AS operatorName,
                   m.circle_name AS circleName,
                   m.validity_end_date AS validityEndDate,
                   m.status,
                   c.customer_id AS customerId,
                   c.full_name AS customerName,
                   c.email
            FROM MOBILE_NUMBERS m
            JOIN CUSTOMERS c ON m.customer_id = c.customer_id
            WHERE m.mobile_number LIKE ?
               OR c.full_name LIKE ?
               OR m.operator_name LIKE ?
            ORDER BY m.mobile_id DESC
        """, q, q, q);
    }

    public int create(MobileRequest request) {
        return jdbc.update("""
            INSERT INTO MOBILE_NUMBERS(customer_id, mobile_number, operator_name, circle_name)
            VALUES (?, ?, ?, ?)
        """, request.customerId(), request.mobileNumber(), request.operatorName(), request.circleName());
    }
}
