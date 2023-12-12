package com.marriott.webapp.api;

import com.marriott.webapp.model.StarwoodMember;
import com.marriott.webapp.model.User;
import com.marriott.webapp.service.AuthenticationFacade;
import com.marriott.webapp.service.EntityNotFoundException;
import com.marriott.webapp.service.MemberService;
import com.marriott.webapp.service.UserService;
import com.marriott.webapp.service.contracts.RegistrationRequest;
import com.marriott.webapp.service.contracts.StarwoodMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final AuthenticationFacade authenticationFacade;
    private final UserService userService;
    private final MemberService memberService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StarwoodMemberResponse> getUserProfile() {

        // Retrieve the user information from the user service or authentication system
        final var user = authenticationFacade.getStarwoodMember();

        if (user.isPresent()) {
            // Return the user information as a JSON response
            return ResponseEntity.ok(StarwoodMemberResponse.builder()
                            .password("")
                            .name(user.get().getName())
                            .surname(user.get().getSurname())
                            .contact(StarwoodMemberResponse.Contact.builder()
                                    .email(user.get().getContact().getEmail())
                                    .phone(user.get().getContact().getPhone())
                                    .build())
                            .address(StarwoodMemberResponse.Address.builder()
                                    .street(user.get().getAddress().getStreet())
                                    .city(user.get().getAddress().getCity())
                                    .state(user.get().getAddress().getState())
                                    .zipcode(user.get().getAddress().getZipcode())
                                    .country(user.get().getAddress().getCountry())
                                    .build())
                    .build());
        } else {
            // Handle the case where the user information is not found
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<StarwoodMember> register(@RequestBody final RegistrationRequest registrationRequest) {
        final var registeredMember = memberService.register(
                StarwoodMember.builder()
                        .name(registrationRequest.getName())
                        .surname(registrationRequest.getSurname())
                        .contact(User.Contact.builder()
                                .email(registrationRequest.getContact().getEmail())
                                .phone(registrationRequest.getContact().getPhone())
                                .build())
                        .address(User.Address.builder()
                                .street(registrationRequest.getAddress().getStreet())
                                .city(registrationRequest.getAddress().getCity())
                                .state(registrationRequest.getAddress().getState())
                                .zipcode(registrationRequest.getAddress().getZipcode())
                                .country(registrationRequest.getAddress().getCountry())
                                .build())
                        .build(),
                registrationRequest.getUsername(),
                registrationRequest.getPassword());

        return ResponseEntity.ok(registeredMember);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody final StarwoodMemberResponse request) {

        final var user = authenticationFacade.getStarwoodMember().orElseThrow(EntityNotFoundException::new);

        memberService.update(user, request, request.getPassword());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public void deleteMember() {
        userService.deleteMember();
    }
}