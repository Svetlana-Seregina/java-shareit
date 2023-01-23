package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoResponse_JsonTest {

    @Autowired
    private JacksonTester<CommentDtoResponse> json;

    @Test
    void testCommentDtoResponse() throws Exception {
        CommentDtoResponse commentDtoResponse = new CommentDtoResponse(
                null,
                "Good stairs",
                "Alex",
                LocalDateTime.of(2023, 1, 23, 7, 10, 0)
        );

        JsonContent<CommentDtoResponse> result = json.write(commentDtoResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Good stairs");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Alex");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-23T07:10:00");
    }
}