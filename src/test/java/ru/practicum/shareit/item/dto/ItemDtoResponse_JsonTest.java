package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoResponse_JsonTest {

    @Autowired
    private JacksonTester<ItemDtoResponse> json;

    @Test
    void testItemDtoResponse() throws Exception {
        ItemDtoResponse itemDtoResponse = new ItemDtoResponse(
                1L,
                "Stairs",
                "New Stairs",
                true,
                null
        );

        JsonContent<ItemDtoResponse> result = json.write(itemDtoResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Stairs");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("New Stairs");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(null);
    }
}