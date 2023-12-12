package com.marriott.webapp.service;

import com.marriott.webapp.config.AESUtil;
import com.marriott.webapp.model.CreditCard;
import com.marriott.webapp.model.MemberRepository;
import com.marriott.webapp.model.StarwoodMember;
import com.marriott.webapp.model.User;
import com.marriott.webapp.service.contracts.CardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MemberRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final AESUtil aesUtil;




    @Transactional
    public void addCreditCard(final CardDTO card) {
        final StarwoodMember member =  authenticationFacade.getStarwoodMember().orElseThrow(() -> new BadCredentialsException(
            MessageFormat.format("principal {0} is not of User type", "StarwoodMember")
        ));

        final var entity = CreditCard.builder()
                .cardNumber(aesUtil.encrypt(card.getCardNumber()))
                .cardName(card.getCardName())
                .expirationDate(aesUtil.encrypt(card.getMonth() + "/" + card.getYear()))
                .lastFourDigits(card.getCardNumber().substring(card.getCardNumber().length() - 4))
                .build();
        member
            .getCreditCards()
            .add(entity);

        entity.setUser(member);

        userRepository.save(member);

    }

    public void deleteMember() {
        User user = authenticationFacade.getStarwoodMember()
                .orElseThrow(EntityNotFoundException::new);

        user.setInactive(true);

        userRepository.save(user);
    }

    @Transactional
    public void deleteCreditCard(long cardId) {
        final StarwoodMember member =  authenticationFacade.getStarwoodMember().orElseThrow(() -> new BadCredentialsException(
            MessageFormat.format("principal {0} is not of User type", "StarwoodMember")
        ));

        member.getCreditCards().removeIf(card -> card.getId() == cardId);
        userRepository.save(member);
    }

    public List<CardDTO> getCreditCard() {
        final StarwoodMember member =  authenticationFacade.getStarwoodMember().orElseThrow(() -> new BadCredentialsException(
            MessageFormat.format("principal {0} is not of User type", "StarwoodMember")
        ));

        return member.getCreditCards()
                .stream()
                .map(card -> CardDTO.builder()
                        .id(String.valueOf(card.getId()))
                        .cardName(card.getCardName())
                        .cardNumber(String.format("**** **** **** %s", card.getLastFourDigits()))
                        .build())
                .toList();
    }
}