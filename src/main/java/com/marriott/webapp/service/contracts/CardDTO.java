package com.marriott.webapp.service.contracts;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardDTO {
    private String id;
    private String cardName;
    private String cardNumber;
    private String month;
    private String year;
}
