package com.marriott.webapp.api;

import com.marriott.webapp.model.Room;
import com.marriott.webapp.model.StarwoodMember;
import com.marriott.webapp.service.AuthenticationFacade;
import com.marriott.webapp.service.contracts.RoomResponse;
import com.marriott.webapp.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final AuthenticationFacade authenticationFacade;

    @GetMapping("/available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
        @RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
        @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate,
        @RequestParam(value = "capacity", required = false) final Integer capacity
    ) {

        final var user = authenticationFacade.getStarwoodMember();

        if (capacity == null) {
            return ResponseEntity.ok(
                roomService
                    .findAvailableRooms(startDate, endDate)
                    .stream()
                    .map(room -> getResponse(user, room))
                    .toList()
            );
        }
        return ResponseEntity.ok(
            roomService.findAvailableRooms(startDate, endDate, capacity)
                .stream()
                .map(room -> getResponse(user, room))
                .toList()
        );
    }

    private RoomResponse getResponse(Optional<StarwoodMember> user, Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomType(room.getRoomType())
                .price(user.isPresent() ? room.getPrice() * 0.9 : room.getPrice())
                .capacity(room.getCapacity())
                .build();
    }


}