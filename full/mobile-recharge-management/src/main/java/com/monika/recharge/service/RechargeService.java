package com.monika.recharge.service;

import com.monika.recharge.dto.RechargeRequest;
import com.monika.recharge.dto.RechargeResponse;
import com.monika.recharge.repository.RechargeRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class RechargeService {
    private final RechargeRepository repository;
    private final SecureRandom random = new SecureRandom();

    public RechargeService(RechargeRepository repository) {
        this.repository = repository;
    }

    public List<RechargeResponse> findAll() {
        return repository.findAll();
    }

    public RechargeResponse findById(long id) {
        return repository.findById(id);
    }

    public RechargeResponse perform(RechargeRequest request) {
        String transactionRef = "TXN-" + System.currentTimeMillis()
                + "-" + (1000 + random.nextInt(9000));

        repository.performRecharge(request, transactionRef);
        return repository.findAll().stream()
                .filter(r -> transactionRef.equals(r.transactionRef()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Recharge was created but could not be retrieved"));
    }
}
