package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoRequestJsonTest {
    @Autowired
    private JacksonTester<BookingDtoRequest> json;

    @Test
    void testBookingDtoRequest() throws Exception {
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
                null,
                LocalDateTime.of(2023, 1, 23, 7, 10, 0),
                LocalDateTime.of(2023, 1, 25, 7, 10, 0)
        );

        JsonContent<BookingDtoRequest> result = json.write(bookingDtoRequest);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-23T07:10:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-01-25T07:10:00");
    }


}