package com.marriott.webapp.api;

import com.marriott.webapp.model.Reservation;
import com.marriott.webapp.model.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.marriott.webapp.model.Reservation.ReservationStatus.ACTIVE;
import static com.marriott.webapp.model.Reservation.ReservationStatus.INACTIVE;


@Component
@RequiredArgsConstructor
public class ReservationStatusUpdateJob {

    private final ReservationRepository reservationRepository;

    @Scheduled(cron = "0 * * * * *")
    public void updateReservations() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Reservation> outdatedReservations = reservationRepository.findByEndDateAndStatus(yesterday, ACTIVE);
        outdatedReservations.forEach(reservation -> reservation.setStatus(INACTIVE));
        reservationRepository.saveAll(outdatedReservations);
    }

}
