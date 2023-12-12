package com.marriott.webapp.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "reservations")
public class Reservation {

    public enum ReservationStatus {
        ACTIVE, CANCELLED, INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "reservation_rooms",
        joinColumns = @JoinColumn(name = "reservation_id"),
        inverseJoinColumns = @JoinColumn(name = "room_id"))
    private Set<Room> rooms;

    @JoinColumn(nullable = false)
    private LocalDate checkInDate;
    @JoinColumn(nullable = false)
    private LocalDate checkOutDate;

    @Setter
    @Enumerated(EnumType.STRING)
    @JoinColumn(nullable = false)
    private ReservationStatus status;

    @JoinColumn(nullable = false)
    private String creditCardNumber;

    @JoinColumn(nullable = false)
    private String creditCardExpiration;

    @JoinColumn(nullable = false)
    private String creditCardLastFourDigits;

    // getters and setters
}