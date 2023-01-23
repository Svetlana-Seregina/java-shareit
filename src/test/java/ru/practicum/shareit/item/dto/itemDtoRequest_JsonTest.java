package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class itemDtoRequest_JsonTest {

    @Autowired
    private JacksonTester<ItemDtoRequest> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemDtoRequest itemDtoRequest = new ItemDtoRequest(
                null,
                "Stairs",
                "New stairs",
                true,
                null,
                null
        );

        JsonContent<ItemDtoRequest> result = json.write(itemDtoRequest);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.name").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.description").isNotBlank();
        assertThat(result).extractingJsonPathBooleanValue("$.available").isNotNull();
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(null);

    }
}