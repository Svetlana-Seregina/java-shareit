package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoCreateJsonTest {

    @Autowired
    private JacksonTester<CommentDtoCreate> json;

    @Test
    void testCommentDtoCreate() throws Exception {
        CommentDtoCreate commentDtoCreate = new CommentDtoCreate(
                "Good stairs"
        );

        JsonContent<CommentDtoCreate> result = json.write(commentDtoCreate);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Good stairs");
    }
}