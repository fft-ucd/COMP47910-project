package com.marriott.webapp.service.contracts;

import com.marriott.webapp.model.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class ReservationResponse {

    private final Long reservationId;
    private final List<RoomViews> rooms;
    private final String guestName;
    private final String guestSurname;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String status;

    public static ReservationResponse fromReservation(Reservation reservation) {
        return ReservationResponse.builder()
                .endDate(reservation.getCheckOutDate())
                .startDate(reservation.getCheckInDate())
                .status(reservation.getStatus().name())
                .guestName(reservation.getUser().getName())
                .guestSurname(reservation.getUser().getSurname())
                .rooms(reservation.getRooms()
                        .stream()
                        .map(room -> RoomViews.builder()
                                .capacity(room.getCapacity())
                                .roomId(room.getId())
                                .roomType(room.getRoomType())
                                .build())
                        .toList())
                .reservationId(reservation.getId())
                .build();
    }

    @Builder
    @Getter
    private static class RoomViews {
        private final Long roomId;
        private final String roomType;
        private final int capacity;
    }
}
