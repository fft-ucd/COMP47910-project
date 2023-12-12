package com.marriott.webapp.service.contracts;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StarwoodMemberResponse {

    @Data
    @Builder
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String zipcode;
        private String country;
    }

    @Data
    @Builder
    public static class Contact {
        private String phone;
        private String email;
    }

    private Long id;

    private String name;
    private String surname;

    private Address address;

    private Contact contact;

    private String password;
}
