package com.example.bookingsystem.service.Impl;

import com.example.bookingsystem.entity.Packages;
import com.example.bookingsystem.repository.PackagesRepository;
import com.example.bookingsystem.service.PackagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PackagesServiceImpl implements PackagesService {

    private final PackagesRepository packagesRepository;

    @Override
    public List<Packages> getAllActivePackages() {
        return packagesRepository.findByIsActiveTrue();
    }

    @Override
    public List<Packages> getActivePackagesByCountry(Integer countryId) {
        return packagesRepository.findActivePackagesByCountry(countryId);
    }

    @Override
    public Packages getPackageById(Integer id) {
        return packagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + id));
    }
}
