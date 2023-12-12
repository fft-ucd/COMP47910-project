package com.marriott.webapp.service.contracts;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class RoomResponse {
    private final String roomType;
    private final Long id;
    private final int capacity;
    private final double price;
}



