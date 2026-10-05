package com.monika.recharge.controller;

import com.monika.recharge.repository.PlanRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plans")
public class PlanController {
    private final PlanRepository repository;

    public PlanController(PlanRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Map<String, Object>> all() {
        return repository.findAll();
    }

    @GetMapping("/above-average-usage")
    public List<Map<String, Object>> aboveAverageUsage() {
        return repository.findAboveAverageUsage();
    }
}
