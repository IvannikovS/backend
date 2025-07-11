package ru.inline.ivannikov.backend.model.types;

import lombok.Getter;

@Getter
public enum NdsRate {
    WITHOUT_NDS("Без НДС"),
    PERCENT_18("18%"),
    PERCENT_20("20%");

    private final String displayName;

    NdsRate(String displayName) {
        this.displayName = displayName;
    }

    public static NdsRate fromDisplayName(String displayName) {
        for (NdsRate rate : values()) {
            if (rate.displayName.equals(displayName)) {
                return rate;
            }
        }
        throw new IllegalArgumentException("Invalid NDS rate: " + displayName);
    }
}
