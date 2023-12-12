package com.marriott.webapp.service.contracts;

import lombok.Data;

@Data
public class RegistrationRequest {

    @Data
    public class Address {
        private String street;
        private String city;
        private String state;
        private String zipcode;
        private String country;
    }

    @Data
    public class Contact {
        private String phone;
        private String email;
    }

    private String name;
    private String surname;
    private Contact contact;
    private Address address;

    private String username;
    private String password;

    // getters and setters
}