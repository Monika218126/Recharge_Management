package com.monika.recharge.controller;

import com.monika.recharge.dto.RechargeRequest;
import com.monika.recharge.dto.RechargeResponse;
import com.monika.recharge.service.RechargeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recharges")
public class RechargeController {
    private final RechargeService service;

    public RechargeController(RechargeService service) {
        this.service = service;
    }

    @GetMapping
    public List<RechargeResponse> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public RechargeResponse one(@PathVariable long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RechargeResponse recharge(@Valid @RequestBody RechargeRequest request) {
        return service.perform(request);
    }
}
