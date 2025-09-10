package com.example.bookingsystem.repository;

import com.example.bookingsystem.entity.Packages;
import com.example.bookingsystem.entity.PurchaseStatus;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.entity.UserPurchases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPurchasesRepository extends JpaRepository<UserPurchases, Integer> {
    List<UserPurchases> findByUser(User user);
    List<UserPurchases> findByUserAndPurchaseStatus(User user, PurchaseStatus status);

    @Query("SELECT up FROM UserPurchases up WHERE up.user = :user AND up.purchaseStatus = 'ACTIVE' AND up.expiredDate >= :currentDate")
    List<UserPurchases> findActivePurchasesByUser(@Param("user") User user, @Param("currentDate") LocalDate currentDate);

    Optional<UserPurchases> findByIdAndUser(Integer id, User user);

    @Query("SELECT COUNT(up) > 0 FROM UserPurchases up WHERE up.user = :user AND up.packages = :package AND up.purchaseStatus = 'ACTIVE'")
    boolean existsActivePurchaseByUserAndPackage(@Param("user") User user, @Param("package") Packages packages);
}