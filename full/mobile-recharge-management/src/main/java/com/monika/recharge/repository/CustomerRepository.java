package com.monika.recharge.repository;

import com.monika.recharge.dto.CustomerRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbc;

    public CustomerRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findAll() {
        return jdbc.queryForList("""
            SELECT customer_id AS customerId,
                   full_name AS fullName,
                   email,
                   address,
                   created_at AS createdAt
            FROM CUSTOMERS
            ORDER BY customer_id DESC
        """);
    }

    public Map<String, Object> findById(long id) {
        return jdbc.queryForMap("""
            SELECT customer_id AS customerId,
                   full_name AS fullName,
                   email,
                   address,
                   created_at AS createdAt
            FROM CUSTOMERS
            WHERE customer_id = ?
        """, id);
    }

    public int create(CustomerRequest request) {
        return jdbc.update("""
            INSERT INTO CUSTOMERS
                (full_name, email, address, password)
            VALUES (?, ?, ?, ?)
        """,
                request.fullName(),
                request.email(),
                request.address(),
                request.password());
    }

    public Map<String, Object> findByEmail(String email) {
        return jdbc.queryForMap("""
            SELECT customer_id AS customerId,
                   full_name AS fullName,
                   email,
                   address,
                   password
            FROM CUSTOMERS
            WHERE LOWER(email) = LOWER(?)
        """, email);
    }
}