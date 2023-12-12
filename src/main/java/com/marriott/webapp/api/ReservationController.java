package com.marriott.webapp.api;

import com.marriott.webapp.model.Reservation;
import com.marriott.webapp.service.contracts.ReservationRequest;
import com.marriott.webapp.service.contracts.ReservationResponse;
import com.marriott.webapp.service.ReservationService;
import com.marriott.webapp.service.contracts.ReservationsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody final ReservationRequest reservationRequest) {
        final Reservation reservation = reservationService.createReservation(
                reservationRequest.getRoomIds(),
                reservationRequest.getGuest(),
                reservationRequest.getStartDate(),
                reservationRequest.getEndDate(),
                reservationRequest.getCreditCard(),
                reservationRequest.getCreditCardExpiration());
        return ResponseEntity.status(201).body(ReservationResponse.fromReservation(reservation));
    }

    @GetMapping("/guest")
    public List<ReservationResponse> getGuestReservations(@RequestParam String name,
                                                          @RequestParam String surname,
                                                          @RequestParam String email) {
        return reservationService.findByNameAndSurnameAndEmail(name, surname, email)
                .stream()
                .map(ReservationResponse::fromReservation)
                .toList();
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable final Long reservationId) {
        final Reservation reservation = reservationService.getReservation(reservationId);
        return ResponseEntity.ok(ReservationResponse.fromReservation(reservation));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservationResponse>> getMyReservations() {
        final List<Reservation> reservations = reservationService.getReservationsForUser();
        return ResponseEntity.ok(reservations.stream().map(ReservationResponse::fromReservation).toList());
    }

    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable final Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/members/book")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationResponse> bookRooms(@RequestBody ReservationsRequest request) {
        final var reservation = reservationService.bookRooms(request.getRoomIds(), request.getStartDate(), request.getEndDate(), request.getCreditCardId());
        return  ResponseEntity.status(201).body(ReservationResponse.fromReservation(reservation));
    }

}