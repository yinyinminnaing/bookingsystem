package com.example.bookingsystem.service;

import java.math.BigDecimal;

public interface PaymentService {
    boolean chargePayment(String userId, BigDecimal amount, String currency);
}