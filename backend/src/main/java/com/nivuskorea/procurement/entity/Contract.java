package com.nivuskorea.procurement.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Contract {

    OPEN_COMPETITION("일반경쟁"),
    RESTRICTED_COMPETITION("제한경쟁");

    private final String description;

    public static Contract fromString(String text) {
        return Arrays.stream(Contract.values())
                .filter(contract -> contract.description.equals(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid BidCategory: " + text));
    }

}
