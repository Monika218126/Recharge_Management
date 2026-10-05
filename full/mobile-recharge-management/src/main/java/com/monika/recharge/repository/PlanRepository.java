package com.monika.recharge.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class PlanRepository {
    private final JdbcTemplate jdbc;

    public PlanRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findAll() {
        return jdbc.queryForList("""
            SELECT plan_id AS planId, plan_name AS planName, operator_name AS operatorName,
                   price, validity_days AS validityDays, data_gb AS dataGb,
                   voice_minutes AS voiceMinutes, sms_count AS smsCount,
                   description, active
            FROM PLANS
            WHERE active = TRUE
            ORDER BY price ASC
        """);
    }

    // Required subquery:
    // plans whose usage count is greater than the average usage count.
    public List<Map<String, Object>> findAboveAverageUsage() {
        return jdbc.queryForList("""
            SELECT
                p.plan_id AS planId,
                p.plan_name AS planName,
                p.operator_name AS operatorName,
                p.price,
                p.validity_days AS validityDays,
                COUNT(r.recharge_id) AS usageCount
            FROM PLANS p
            LEFT JOIN RECHARGES r ON p.plan_id = r.plan_id
            GROUP BY p.plan_id, p.plan_name, p.operator_name, p.price, p.validity_days
            HAVING COUNT(r.recharge_id) > (
                SELECT AVG(plan_usage.usage_count)
                FROM (
                    SELECT p2.plan_id, COUNT(r2.recharge_id) AS usage_count
                    FROM PLANS p2
                    LEFT JOIN RECHARGES r2 ON p2.plan_id = r2.plan_id
                    GROUP BY p2.plan_id
                ) AS plan_usage
            )
            ORDER BY usageCount DESC
        """);
    }
}
