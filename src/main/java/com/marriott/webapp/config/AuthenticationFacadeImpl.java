package com.marriott.webapp.config;

import com.marriott.webapp.model.StarwoodMember;
import com.marriott.webapp.service.AuthenticationFacade;
import com.marriott.webapp.service.MemberService;
import com.marriott.webapp.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationFacadeImpl implements AuthenticationFacade {

    private final MemberService memberService;

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public Optional<StarwoodMember> getStarwoodMember() {
        return Optional.ofNullable(getAuthentication())
                .map(Authentication::getPrincipal)
                .map(context -> {
                    if (context instanceof UserDetailsImpl) {
                        return (UserDetailsImpl) context;
                    }
                    return null;
                })
                .map(userDetails -> memberService.getByUsername(userDetails.getUsername()))
                .filter(member -> member.getType().equals(StarwoodMember.STARWOOD))
                .map(member -> (StarwoodMember) member);
    }
}