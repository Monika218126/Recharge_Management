package com.monika.recharge.controller;

import com.monika.recharge.dto.CustomerRequest;
import com.monika.recharge.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerRepository repository;

    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Map<String, Object>> all() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Map<String, Object> one(@PathVariable long id) {
        return repository.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@Valid @RequestBody CustomerRequest request) {
        repository.create(request);
        return Map.of("success", true, "message", "Customer created successfully");
    }
}
