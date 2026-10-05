package com.monika.recharge.repository;

import com.monika.recharge.dto.RechargeRequest;
import com.monika.recharge.dto.RechargeResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RechargeRepository {

    private final JdbcTemplate jdbc;

    public RechargeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Required JOIN query
    private static final String DETAILS_SQL = """
        SELECT recharge_id,
               customer_name,
               mobile_number,
               operator_name,
               plan_name,
               amount,
               validity_days,
               recharge_date,
               payment_method,
               transaction_ref,
               recharge_status,
               mobile_status,
               validity_end_date
        FROM vw_recharge_details
        """;

    // Get all recharge details
    public List<RechargeResponse> findAll() {
        return jdbc.query(
                DETAILS_SQL + " ORDER BY recharge_id DESC",
                this::map
        );
    }

    // Get recharge by ID
    public RechargeResponse findById(long id) {
        return jdbc.queryForObject(
                DETAILS_SQL + " WHERE recharge_id = ?",
                this::map,
                id
        );
    }

    // Perform recharge using stored procedure
    public void performRecharge(
            RechargeRequest request,
            String transactionRef) {

        jdbc.update(
                "CALL perform_recharge(?, ?, ?, ?)",
                request.mobileId(),
                request.planId(),
                request.paymentMethod(),
                transactionRef
        );
    }

    // Convert ResultSet row into RechargeResponse
    private RechargeResponse map(
            ResultSet rs,
            int rowNum) throws SQLException {

        return new RechargeResponse(
                rs.getLong("recharge_id"),
                rs.getString("customer_name"),
                rs.getString("mobile_number"),
                rs.getString("operator_name"),
                rs.getString("plan_name"),
                rs.getBigDecimal("amount"),
                rs.getInt("validity_days"),
                rs.getTimestamp("recharge_date").toLocalDateTime(),
                rs.getString("payment_method"),
                rs.getString("transaction_ref"),
                rs.getString("recharge_status"),
                rs.getString("mobile_status"),
                rs.getDate("validity_end_date") == null
                        ? null
                        : rs.getDate("validity_end_date").toLocalDate()
        );
    }
}