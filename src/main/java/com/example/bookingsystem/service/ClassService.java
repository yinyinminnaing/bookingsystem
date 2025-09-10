package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.ClassResponseDTO;

import java.util.List;

public interface ClassService {
    List<ClassResponseDTO> getAvailableClassesForUser(Integer userId);
    List<ClassResponseDTO> getClassesByCountry(Integer countryId);
}
