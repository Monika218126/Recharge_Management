# Architecture

Browser
  |
  | HTTP/JSON
  v
Spring Boot REST Controllers
  |
  v
Services
  |
  v
JdbcTemplate Repositories
  |
  v
MySQL
  |
  +-- CUSTOMERS
  +-- MOBILE_NUMBERS
  +-- PLANS
  +-- RECHARGES
  +-- FUNCTION calculate_recharge_validity
  +-- PROCEDURE perform_recharge
  +-- TRIGGER trg_recharges_after_insert
  +-- VIEW vw_recharge_details

Recharge flow:

Frontend
 -> POST /api/recharges
 -> RechargeController
 -> RechargeService
 -> CALL perform_recharge(...)
 -> INSERT into RECHARGES
 -> AFTER INSERT trigger
 -> calculate_recharge_validity(...)
 -> UPDATE MOBILE_NUMBERS
 -> return recharge details
