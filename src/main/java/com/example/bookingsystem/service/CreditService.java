package com.example.bookingsystem.service;

import com.example.bookingsystem.entity.UserPurchases;

public interface CreditService {
    void deductCredits(UserPurchases userPurchase, int credits);
    void refundCredits(UserPurchases userPurchase, int credits);
}
