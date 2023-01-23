package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoResponseJsonTest {

    @Autowired
    private JacksonTester<BookingDtoResponse> json;

    @Test
    void testBookingDtoResponse() throws Exception {
        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse(
                null,
                LocalDateTime.of(2023, 1, 23, 7, 10, 0),
                LocalDateTime.of(2023, 1, 25, 7, 10, 0),
                BookingState.WAITING,
                new BookingDtoResponse.User(1L),
                new BookingDtoResponse.Item(1L, "Alex")
        );

        JsonContent<BookingDtoResponse> result = json.write(bookingDtoResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-23T07:10:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-01-25T07:10:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Alex");
    }

}