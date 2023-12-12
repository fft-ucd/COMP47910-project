package com.marriott.webapp.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = Guest.GUEST)
public class Guest extends User {

    public static final String GUEST = "guest";

    // getters and setters

    @Override
    public String getType() {
        return GUEST;
    }
}
