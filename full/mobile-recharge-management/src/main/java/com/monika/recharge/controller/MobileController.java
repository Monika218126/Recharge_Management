package com.monika.recharge.controller;

import com.monika.recharge.dto.MobileRequest;
import com.monika.recharge.repository.MobileRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mobiles")
public class MobileController {
    private final MobileRepository repository;

    public MobileController(MobileRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Map<String, Object>> all(@RequestParam(required = false) String keyword) {
        return repository.findAll(keyword);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@Valid @RequestBody MobileRequest request) {
        repository.create(request);
        return Map.of("success", true, "message", "Mobile number created successfully");
    }
}
