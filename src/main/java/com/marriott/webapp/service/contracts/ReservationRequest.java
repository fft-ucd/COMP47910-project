package com.marriott.webapp.service.contracts;


import com.marriott.webapp.model.Guest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Getter
public class ReservationRequest {

    private Set<Long> roomIds;
    private Guest guest;
    private LocalDate startDate;
    private LocalDate endDate;
    private String creditCard;
    private String creditCardExpiration;

    // getters and setters
}