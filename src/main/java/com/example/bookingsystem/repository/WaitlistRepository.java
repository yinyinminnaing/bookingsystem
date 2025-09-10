package com.example.bookingsystem.repository;

import com.example.bookingsystem.entity.Classes;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.entity.WaitingLists;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface WaitlistRepository extends JpaRepository<WaitingLists, Long> {
    List<WaitingLists> findByUser(User user);
    List<WaitingLists> findByClasses(Classes classes);

    @Query("SELECT w FROM WaitingLists w WHERE w.user = :user AND w.action = 'PENDING'")
    List<WaitingLists> findPendingWaitlistsByUser(@Param("user") User user);

    Optional<WaitingLists> findByUserAndClasses(User user, Classes classes);

    @Query("SELECT w FROM WaitingLists w WHERE w.classes = :classes AND w.action = 'PENDING' ORDER BY w.joinedDate ASC")
    List<WaitingLists> findPendingWaitlistsByClassOrderByJoinTime(@Param("classes") Classes classes);

    //long countByClassesAndStatus(Classes classes, WaitingAction status);
}