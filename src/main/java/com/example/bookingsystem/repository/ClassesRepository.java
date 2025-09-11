package com.example.bookingsystem.repository;

import com.example.bookingsystem.entity.Classes;
import com.example.bookingsystem.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


public interface ClassesRepository extends JpaRepository<Classes, Integer> {

    List<Classes> findByCountryAndIsCompletedFalse(Country country);

    @Query("SELECT c FROM Classes c WHERE c.country = :country AND c.isCompleted = false AND c.startTime > :currentTime")
    List<Classes> findUpcomingClassesByCountry(@Param("country") Country country, @Param("currentTime") LocalTime currentTime);

    long countByCountry(Country country);

    @Query("SELECT c FROM Classes c WHERE c.endTime < :currentTime AND EXISTS " +
            "(SELECT w FROM WaitingLists w WHERE w.classes = c AND w.action = 'PENDING')")
    List<Classes> findEndedClassesWithWaitlist(@Param("currentTime") LocalDateTime currentTime);
}