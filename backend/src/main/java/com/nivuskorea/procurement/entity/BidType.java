package com.nivuskorea.procurement.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BidType {

    PRE_STANDARD("사전규격"),
    BID_ANNOUNCEMENT("입찰공고");

    private final String description;

    public static BidType fromString(String text) {
        return Arrays.stream(BidType.values())
                .filter(bidType -> bidType.description.equals(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid BidCategory: " + text));
    }
}
