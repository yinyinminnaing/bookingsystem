package com.example.bookingsystem.service.Impl;

import com.example.bookingsystem.entity.UserPurchases;
import com.example.bookingsystem.repository.UserPurchasesRepository;
import com.example.bookingsystem.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final UserPurchasesRepository userPurchasesRepository;
    @Override
    @Transactional
    public void deductCredits(UserPurchases userPurchase, int credits) {
        // This would need to be implemented based on your credit system
        // For now, we'll assume UserPurchases has a credit field
        if (userPurchase.getRemainingCredits() < credits) {
            throw new RuntimeException("Insufficient credits");
        }
        userPurchase.setRemainingCredits(userPurchase.getRemainingCredits() - credits);
        userPurchasesRepository.save(userPurchase);
    }

    @Override
    @Transactional
    public void refundCredits(UserPurchases userPurchase, int credits) {
        userPurchase.setRemainingCredits(userPurchase.getRemainingCredits() + credits);
        userPurchasesRepository.save(userPurchase);
    }
}
