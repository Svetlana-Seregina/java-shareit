package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {
    WAITING, APPROVED, REJECTED, CANCELED, ALL, FUTURE, PAST, CURRENT;

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
