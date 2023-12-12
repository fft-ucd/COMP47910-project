package com.marriott.webapp.api;

import com.marriott.webapp.service.contracts.CardDTO;
import com.marriott.webapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/creditcards")
public class CreditCardController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addCreditCard(@RequestBody final CardDTO card) {
        userService.addCreditCard(card);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cardId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCreditCard(@PathVariable final long cardId) {
        userService.deleteCreditCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CardDTO>> getCreditCard() {
        return ResponseEntity.ok(userService.getCreditCard());
    }
}