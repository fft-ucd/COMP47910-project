package com.marriott.webapp.service;

import com.marriott.webapp.model.MemberRepository;
import com.marriott.webapp.model.StarwoodMember;
import com.marriott.webapp.model.User;
import com.marriott.webapp.service.contracts.StarwoodMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // You can use Spring's BCryptPasswordEncoder

    @Transactional
    public StarwoodMember register(final StarwoodMember member, final String username, final String password) {
        if (memberRepository.findByCredentialsUsername(username).isPresent()) {
            throw new SystemConflictException("Username already exists");
        }

        final var user = memberRepository.findByContactEmail(member.getContact().getEmail());

        if (user.isPresent() && user.get().getType().equals(StarwoodMember.STARWOOD) ) {
            throw new SystemConflictException("Email already exists");
        } else if (user.isPresent()) {
            memberRepository.updateType(user.get().getId(), StarwoodMember.STARWOOD);
            user.ifPresent(value -> {
                member.setId(value.getId());
                member.setCreditCards(new ArrayList<>());
                member.setReservations(new ArrayList<>());
                member.setInactive(false);
            });
        }

        if (password.length() < 8) {
            throw new SystemConflictException("Password must be at least 8 characters long");
        }

        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password));

        return memberRepository.save(member);
    }

    @Transactional
    public void update(final StarwoodMember member, final StarwoodMemberResponse request, final String password) {
        final var user = memberRepository.findByContactEmail(request.getContact().getEmail());

        if (user.isPresent() && !user.get().getId().equals(member.getId())) {
            throw new SystemConflictException("Email does not exist");
        }

        member.setName(request.getName());
        member.setSurname(request.getSurname());
        member.setContact(User.Contact.builder()
                .email(request.getContact().getEmail())
                .phone(request.getContact().getPhone())
                .build());
        member.setAddress(User.Address.builder()
                .street(request.getAddress().getStreet())
                .city(request.getAddress().getCity())
                .state(request.getAddress().getState())
                .zipcode(request.getAddress().getZipcode())
                .country(request.getAddress().getCountry())
                .build());;


        if (StringUtils.hasText(password) && password.length() < 8) {
            throw new SystemConflictException("Password must be at least 8 characters long");
        }

        if (StringUtils.hasText(password)) {
            member.setPassword(passwordEncoder.encode(password));
        }

        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public User getByUsername(final String username) {
        return memberRepository.findByCredentialsUsername(username)
            .filter(user -> !user.isInactive())
            .orElseThrow(EntityNotFoundException::new);
    }
}