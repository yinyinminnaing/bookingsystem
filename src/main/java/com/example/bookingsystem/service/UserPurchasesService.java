package com.example.bookingsystem.service;

import com.example.bookingsystem.entity.UserPurchases;

import java.util.List;

public interface UserPurchasesService {
    List<UserPurchases> getUserPurchases(Integer userId);
    List<UserPurchases> getActiveUserPurchases(Integer userId);
    UserPurchases purchasePackage(Integer userId, Integer packageId);
}
