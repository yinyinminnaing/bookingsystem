
package com.example.bookingsystem.service.Impl;

import com.example.bookingsystem.dto.ClassResponseDTO;
import com.example.bookingsystem.entity.Classes;
import com.example.bookingsystem.entity.Country;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.entity.UserPurchases;
import com.example.bookingsystem.repository.BookingRepository;
import com.example.bookingsystem.repository.ClassesRepository;
import com.example.bookingsystem.repository.CountryRepository;
import com.example.bookingsystem.service.ClassService;
import com.example.bookingsystem.service.UserPurchasesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {
    private final ClassesRepository classesRepository;
    private final CountryRepository countryRepository;
    private final BookingRepository bookingRepository;
    private final UserPurchasesService userPurchasesService;

    @Override
    public List<ClassResponseDTO> getAvailableClassesForUser(Integer userId) {
        // Get user's active purchases to determine accessible countries
        List<UserPurchases> activePurchases = userPurchasesService.getActiveUserPurchases(userId);

        // Get unique countries from active purchases
        List<Country> accessibleCountries = activePurchases.stream()
                .map(purchase -> purchase.getPackages().getCountry())
                .distinct()
                .collect(Collectors.toList());

        // Get upcoming classes for accessible countries
        LocalTime currentTime = LocalTime.now();
        return accessibleCountries.stream()
                .flatMap(country -> classesRepository.findUpcomingClassesByCountry(country, currentTime).stream())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClassResponseDTO> getClassesByCountry(Integer countryId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new RuntimeException("Country not found"));

        LocalTime currentTime = LocalTime.now();
        return classesRepository.findUpcomingClassesByCountry(country, currentTime).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ClassResponseDTO convertToDTO(Classes classes) {
        int currentBookings = bookingRepository.countConfirmedBookingsForClass(classes);
        boolean hasAvailableSlots = currentBookings < classes.getMaxCapacity();

        return ClassResponseDTO.builder()
                .id(classes.getId())
                .className(classes.getClassName())
                .startTime(classes.getStartTime())
                .endTime(classes.getEndTime())
                .courseDuration(classes.getCourseDuration())
                .requiredCredits(classes.getRequiredCredits())
                .maxCapacity(classes.getMaxCapacity())
                .currentBookings(currentBookings)
                .hasAvailableSlots(hasAvailableSlots)
                .countryId(classes.getCountry().getId())
                .countryName(classes.getCountry().getName())
                .build();
    }
}