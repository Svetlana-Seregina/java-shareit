package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoResponseJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDtoResponse> json;

    @Test
    void testItemRequestDtoResponse() throws Exception {
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse(
                1L,
                "Need for 4 chairs",
                LocalDateTime.of(2023, 1, 23, 7, 10, 0)
        );

        JsonContent<ItemRequestDtoResponse> result = json.write(itemRequestDtoResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need for 4 chairs");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-23T07:10:00");
    }
}