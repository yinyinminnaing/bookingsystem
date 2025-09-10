package com.example.bookingsystem.service;

import com.example.bookingsystem.entity.Packages;

import java.util.List;

public interface PackagesService {
    List<Packages> getAllActivePackages();
    List<Packages> getActivePackagesByCountry(Integer countryId);
    Packages getPackageById(Integer id);
}
