package com.nivuskorea.procurement.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Category {
    GOODS("물품"),
    GENERAL_SERVICE("일반용역"),
    TECHNICAL_SERVICE("기술용역");

    private final String description;

    public static Category fromString(String text) {
        return Arrays.stream(Category.values())
                .filter(category -> category.description.equals(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid BidCategory: " + text));
    }

}
