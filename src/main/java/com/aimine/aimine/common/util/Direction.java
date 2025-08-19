package com.aimine.aimine.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Direction {
    ASC("asc", "오름차순"),
    DESC("desc", "내림차순");

    private final String value;
    private final String description;

    public static Direction fromString(String value) {
        if (value == null) {
            return DESC; // 기본값
        }

        for (Direction direction : Direction.values()) {
            if (direction.value.equalsIgnoreCase(value)) {
                return direction;
            }
        }

        return DESC; // 기본값
    }
}