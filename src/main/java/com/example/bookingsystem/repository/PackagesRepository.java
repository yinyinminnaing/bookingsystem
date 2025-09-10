package com.example.bookingsystem.repository;

import com.example.bookingsystem.entity.Packages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackagesRepository extends JpaRepository<Packages, Integer> {
    List<Packages> findByIsActiveTrue();
    List<Packages> findByCountryIdAndIsActiveTrue(Integer countryId);

    @Query("SELECT p FROM Packages p WHERE p.isActive = true AND p.country.id = :countryId")
    List<Packages> findActivePackagesByCountry(@Param("countryId") Integer countryId);
}