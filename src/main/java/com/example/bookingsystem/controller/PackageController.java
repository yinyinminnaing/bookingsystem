package com.example.bookingsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.bookingsystem.dto.PackageResponseDTO;
import com.example.bookingsystem.dto.PurchaseRequestDTO;
import com.example.bookingsystem.dto.PurchaseResponseDTO;
import com.example.bookingsystem.entity.Packages;
import com.example.bookingsystem.entity.UserPurchases;
import com.example.bookingsystem.service.PackagesService;
import com.example.bookingsystem.service.UserPurchasesService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class PackageController {

    private final PackagesService packagesService;
    private final UserPurchasesService userPurchasesService;

    @GetMapping
    public ResponseEntity<List<PackageResponseDTO>> getAllPackages() {
        List<Packages> packages = packagesService.getAllActivePackages();
        List<PackageResponseDTO> response = packages.stream()
                .map(this::convertToPackageDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/country/{countryId}")
    public ResponseEntity<List<PackageResponseDTO>> getPackagesByCountry(@PathVariable Integer countryId) {
        List<Packages> packages = packagesService.getActivePackagesByCountry(countryId);
        List<PackageResponseDTO> response = packages.stream()
                .map(this::convertToPackageDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/purchase/{userId}")
    public ResponseEntity<PurchaseResponseDTO> purchasePackage(
            @PathVariable Integer userId,
            @Valid @RequestBody PurchaseRequestDTO request) {
        UserPurchases purchase = userPurchasesService.purchasePackage(userId, request.getPackageId());
        return ResponseEntity.ok(convertToPurchaseDTO(purchase));
    }

    @GetMapping("/user/{userId}/purchases")
    public ResponseEntity<List<PurchaseResponseDTO>> getUserPurchases(@PathVariable Integer userId) {
        List<UserPurchases> purchases = userPurchasesService.getUserPurchases(userId);
        List<PurchaseResponseDTO> response = purchases.stream()
                .map(this::convertToPurchaseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/active-purchases")
    public ResponseEntity<List<PurchaseResponseDTO>> getActiveUserPurchases(@PathVariable Integer userId) {
        List<UserPurchases> purchases = userPurchasesService.getActiveUserPurchases(userId);
        List<PurchaseResponseDTO> response = purchases.stream()
                .map(this::convertToPurchaseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private PackageResponseDTO convertToPackageDTO(Packages packages) {
        return PackageResponseDTO.builder()
                .id(packages.getId())
                .packageName(packages.getPackageName())
                .credits(packages.getCredits())
                .prices(packages.getPrices())
                .expiredDate(packages.getExpiredDate())
                .status(packages.getStatus())
                .isActive(packages.isActive())
                .countryId(packages.getCountry().getId())
                .countryName(packages.getCountry().getName())
                .build();
    }

    private PurchaseResponseDTO convertToPurchaseDTO(UserPurchases purchase) {
        return PurchaseResponseDTO.builder()
                .id(purchase.getId())
                .purchasedDate(purchase.getPurchasedDate())
                .expiredDate(purchase.getExpiredDate())
                .purchaseStatus(purchase.getPurchaseStatus())
                .packageId(purchase.getPackages().getId())
                .packageName(purchase.getPackages().getPackageName())
                .credits(purchase.getPackages().getCredits())
                .build();
    }
}
