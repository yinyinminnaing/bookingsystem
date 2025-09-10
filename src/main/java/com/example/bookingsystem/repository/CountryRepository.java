package com.example.bookingsystem.repository;

import com.example.bookingsystem.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface CountryRepository extends JpaRepository<Country, Integer> {
    Optional<Country> findByName(String name);
    Optional<Country> findByCode(String code);
}