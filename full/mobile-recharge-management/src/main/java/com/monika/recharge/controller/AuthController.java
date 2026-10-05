package com.monika.recharge.controller;

import com.monika.recharge.dto.LoginRequest;
import com.monika.recharge.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CustomerRepository customerRepository;

    public AuthController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping("/login")
    public Map<String, Object> login(
            @Valid @RequestBody LoginRequest request) {

        try {
            Map<String, Object> customer =
                    customerRepository.findByEmail(request.email());

            String storedPassword =
                    String.valueOf(customer.get("password"));

            if (!storedPassword.equals(request.password())) {
                return Map.of(
                        "success", false,
                        "message", "Invalid email or password"
                );
            }

            return Map.of(
                    "success", true,
                    "message", "Login successful",
                    "customerId", customer.get("customerId"),
                    "fullName", customer.get("fullName"),
                    "email", customer.get("email")
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "Invalid email or password"
            );
        }
    }
}