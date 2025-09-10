package com.example.bookingsystem.service.Impl;

import com.example.bookingsystem.entity.Packages;
import com.example.bookingsystem.entity.PurchaseStatus;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.entity.UserPurchases;
import com.example.bookingsystem.repository.UserPurchasesRepository;
import com.example.bookingsystem.repository.UserRepository;
import com.example.bookingsystem.service.PackagesService;
import com.example.bookingsystem.service.UserPurchasesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPurchasesServiceImpl implements UserPurchasesService {

    private final UserPurchasesRepository userPurchasesRepository;
    private final UserRepository userRepository;
    private final PackagesService packagesService;

    @Override
    public List<UserPurchases> getUserPurchases(Integer userId) {
        User user = getUserById(userId);
        return userPurchasesRepository.findByUser(user);
    }

    @Override
    public List<UserPurchases> getActiveUserPurchases(Integer userId) {
        User user = getUserById(userId);
        return userPurchasesRepository.findActivePurchasesByUser(user, LocalDate.now());
    }

    @Override
    public UserPurchases purchasePackage(Integer userId, Integer packageId) {
        User user = getUserById(userId);
        Packages packages = packagesService.getPackageById(packageId);

        // Check if user already has an active purchase for this package
        if (userPurchasesRepository.existsActivePurchaseByUserAndPackage(user, packages)) {
            throw new RuntimeException("User already has an active purchase for this package");
        }

        // Create new purchase
        UserPurchases purchase = UserPurchases.builder()
                .user(user)
                .packages(packages)
                .purchasedDate(LocalDate.now())
                .expiredDate(packages.getExpiredDate())
                .purchaseStatus(PurchaseStatus.ACTIVE)
                .build();

        return userPurchasesRepository.save(purchase);
    }

    private User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
}
