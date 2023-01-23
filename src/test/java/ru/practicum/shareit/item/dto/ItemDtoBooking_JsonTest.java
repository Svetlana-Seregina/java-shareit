package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoBooking_JsonTest {

    @Autowired
    private JacksonTester<ItemDtoBooking> json;

    @Test
    void testItemDtoBooking() throws Exception {
        ItemDtoBooking itemDtoBooking = new ItemDtoBooking(
                1L,
                "Chairs",
                "4 chairs",
                true
        );

        JsonContent<ItemDtoBooking> result = json.write(itemDtoBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Chairs");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("4 chairs");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }
}