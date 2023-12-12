package com.marriott.webapp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE NOT EXISTS " +
            "(SELECT res FROM Reservation res " +
            "JOIN res.rooms ro " +
            "WHERE ro = r " +
            "AND res.status = 'ACTIVE' " +
            "AND (res.checkInDate <= :endDate " +
            "AND res.checkOutDate >= :startDate))")
    List<Room> findAvailableRooms(LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM Room r WHERE r.capacity >= :capacity AND NOT EXISTS " +
            "(SELECT res FROM Reservation res " +
            "JOIN res.rooms ro " +
            "WHERE ro = r " +
            "AND res.status = 'ACTIVE' " +
            "AND (res.checkInDate <= :endDate " +
            "AND res.checkOutDate >= :startDate))")
    List<Room> findAvailableRooms(LocalDate startDate, LocalDate endDate, Integer capacity);


}