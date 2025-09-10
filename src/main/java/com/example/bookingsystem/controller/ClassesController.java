package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.ClassResponseDTO;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassesController {

    private final ClassService classService;

    @GetMapping("/classes/available")
    public ResponseEntity<List<ClassResponseDTO>> getAvailableClasses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<ClassResponseDTO> classes = classService.getAvailableClassesForUser(user.getId());
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/classes/country/{countryId}")
    public ResponseEntity<List<ClassResponseDTO>> getClassesByCountry(@PathVariable Integer countryId) {
        List<ClassResponseDTO> classes = classService.getClassesByCountry(countryId);
        return ResponseEntity.ok(classes);
    }

}
