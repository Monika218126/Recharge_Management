package com.monika.recharge.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;

@Repository
public class DashboardRepository {
    private final JdbcTemplate jdbc;

    public DashboardRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, Object> stats() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("customers", scalar("SELECT COUNT(*) FROM CUSTOMERS"));
        result.put("mobileNumbers", scalar("SELECT COUNT(*) FROM MOBILE_NUMBERS"));
        result.put("activeMobiles", scalar("SELECT COUNT(*) FROM MOBILE_NUMBERS WHERE status='ACTIVE'"));
        result.put("plans", scalar("SELECT COUNT(*) FROM PLANS WHERE active=TRUE"));
        result.put("totalRecharges", scalar("SELECT COUNT(*) FROM RECHARGES WHERE status='SUCCESS'"));
        result.put("totalRevenue", scalar("SELECT COALESCE(SUM(amount),0) FROM RECHARGES WHERE status='SUCCESS'"));
        return result;
    }

    private Object scalar(String sql) {
        return jdbc.queryForObject(sql, Object.class);
    }
}
