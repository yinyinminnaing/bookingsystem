package com.example.bookingsystem.service.Impl;

import com.example.bookingsystem.service.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements com.example.bookingsystem.service.PaymentService {

    @Override
    public boolean chargePayment(String userId, BigDecimal amount, String currency) {
        System.out.println("PaymentService: " + amount + " " + currency + " charged successfully for user " + userId);
        return true;
    }
}