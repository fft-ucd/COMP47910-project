package com.marriott.webapp.service;

import com.marriott.webapp.config.AESUtil;
import com.marriott.webapp.model.CreditCard;
import com.marriott.webapp.model.Guest;
import com.marriott.webapp.model.MemberRepository;
import com.marriott.webapp.model.Reservation;
import com.marriott.webapp.model.ReservationRepository;
import com.marriott.webapp.model.Room;
import com.marriott.webapp.model.RoomRepository;
import com.marriott.webapp.model.StarwoodMember;
import com.marriott.webapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.marriott.webapp.model.Reservation.ReservationStatus;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final AESUtil aesUtil;

    @Transactional
    public Reservation createReservation(final Set<Long> roomIds,
                                         final Guest guest,
                                         final LocalDate startDate,
                                         final LocalDate endDate,
                                         final String creditCardNumber,
                                         final String creditCardExpiration) {

        if (startDate.isAfter(endDate)) {
            throw new ClientErrorException("Invalid date range");
        }

        if (roomIds.isEmpty()) {
            throw new ClientErrorException("At least one room must be selected");
        }

        final List<Room> rooms = roomRepository.findAvailableRooms(startDate, endDate)
                .stream()
                .filter(room -> roomIds.contains(room.getId()))
                .toList();

        if (rooms.size() != roomIds.size()) {
            throw new ClientErrorException("Invalid room selection");
        }

        Optional<User> reservationGuest = userRepository.findByContactEmail(guest.getContact().getEmail())
                .map(user -> {
                    if (user.getType().equals(Guest.GUEST)) {
                        var guestUser = (Guest) user;
                        guestUser.setName(guest.getName());
                        guestUser.setSurname(guest.getSurname());
                        guestUser.setContact(guest.getContact());
                        guestUser.setSurname(guest.getSurname());
                        guestUser.setAddress(guest.getAddress());
                        return guestUser;
                    }

                    return user;
                });

        //CHECK IF THE USER IS A MEMBER
        if (reservationGuest.isPresent() && !reservationGuest.get().getType().equals(Guest.GUEST)) {
            throw new ClientErrorException("Invalid guest");
        }

        if (reservationGuest.isEmpty()) {
            //CHECK IF IT IS A NEW USER
            final var user = userRepository.save(guest);
            reservationGuest = Optional.of(user);
        } else {
            userRepository.save(reservationGuest.get());
        }

        // This is a basic implementation. You should add more logic here,
        // like checking if the room is available for the specified dates.
        final Reservation reservation = Reservation.builder()
                                            .rooms(Set.copyOf(rooms))
                                            .user(reservationGuest.get())
                                            .checkInDate(startDate)
                                            .checkOutDate(endDate)
                                            .status(ReservationStatus.ACTIVE)
                                            // You should probably encrypt the credit card number and not store it in plain text.
                                            .creditCardNumber(aesUtil.encrypt(creditCardNumber))
                                            .creditCardExpiration(aesUtil.encrypt(creditCardExpiration))
                                            .creditCardLastFourDigits(creditCardNumber.substring(creditCardNumber.length() - 4))
                                            .build();

        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getReservationsForUser() {
        return reservationRepository.findAllByUserId(getAuthenticatedMember().getId());
    }

    @Transactional
    public void cancelReservation(final Long reservationId) {
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ClientErrorException("Invalid reservation id"));

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new SystemConflictException("Cannot cancel a reservation that is not active");
        }

        final LocalDate now = LocalDate.now();

        if (now.isAfter(reservation.getCheckInDate().minusDays(1))) {
            throw new SystemConflictException("Reservation can only be cancelled within 24 hours from the check-in date.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation bookRooms(final List<Long> roomIds,
                                 final LocalDate startDate,
                                 final LocalDate endDate,
                                 final Long creditCardId) {

        final var member = getAuthenticatedMember();
        final var creditCard = getCreditCard(member, creditCardId);

        final List<Room> rooms = roomRepository.findAllById(roomIds);

        final var reservation = createReservation(member, rooms, startDate, endDate, creditCard);

        return reservationRepository.save(reservation);
    }

    private StarwoodMember getAuthenticatedMember() {
        return authenticationFacade.getStarwoodMember().orElseThrow();
    }

    private CreditCard getCreditCard(final StarwoodMember member, final Long creditCardId) {
        return member.getCreditCards().stream()
                .filter(creditCard -> creditCard.getId().equals(creditCardId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid credit card id"));
    }

    private Reservation createReservation(final StarwoodMember member,
                                                final List<Room> rooms,
                                                final LocalDate startDate,
                                                final LocalDate endDate,
                                                final CreditCard creditCard) {

        return Reservation.builder()
                    .rooms(Set.copyOf(rooms))
                    .user(member)
                    .checkInDate(startDate)
                    .checkOutDate(endDate)
                    .status(ReservationStatus.ACTIVE)
                    .creditCardNumber(creditCard.getCardNumber())
                    .creditCardExpiration(creditCard.getExpirationDate())
                    .creditCardLastFourDigits(creditCard.getLastFourDigits())
                    .build();
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByNameAndSurnameAndEmail(final String name, final String surname, final String email) {
        return reservationRepository.findByNameAndSurnameAndEmail(name, surname, email)
                .stream()
                .filter(reservation -> reservation.getUser().getType().equals(Guest.GUEST))
                .toList();
    }

    @Transactional(readOnly = true)
    public Reservation getReservation(final Long reservationId) {
        return reservationRepository.findById(reservationId)
                .filter(reservation -> reservation.getUser().getType().equals(Guest.GUEST))
                .orElseThrow(EntityNotFoundException::new);
    }
}