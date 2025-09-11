package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.ClassResponseDTO;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Classes", description = "Class information and availability endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class ClassesController {

    private final ClassService classService;

    @GetMapping("/classes/available")
    @Operation(summary = "Get available classes", description = "Get classes available for booking based on user's active packages")
    public ResponseEntity<List<ClassResponseDTO>> getAvailableClasses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<ClassResponseDTO> classes = classService.getAvailableClassesForUser(user.getId());
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/classes/country/{countryId}")
    @Operation(summary = "Get classes by country", description = "Retrieve all available classes for a specific country")
    @Parameter(name = "countryId", description = "ID of the country", required = true)
    public ResponseEntity<List<ClassResponseDTO>> getClassesByCountry(@PathVariable Integer countryId) {
        List<ClassResponseDTO> classes = classService.getClassesByCountry(countryId);
        return ResponseEntity.ok(classes);
    }

}
