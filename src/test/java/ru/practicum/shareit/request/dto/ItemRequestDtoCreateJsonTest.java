package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoCreateJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDtoCreate> json;

    @Test
    void testItemRequestDtoCreate() throws Exception {
        ItemRequestDtoCreate itemRequestDtoCreate = new ItemRequestDtoCreate(
                "Need for 4 chairs"
        );

        JsonContent<ItemRequestDtoCreate> result = json.write(itemRequestDtoCreate);

        assertThat(result).extractingJsonPathStringValue("$.description").isNotBlank();
    }
}