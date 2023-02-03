package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoBookingJsonTest {

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
                itemDtoBooking.setLastBooking(new ItemDtoBooking.Booking(1L, 1L));
                itemDtoBooking.setNextBooking(new ItemDtoBooking.Booking(1L, 1L));
                itemDtoBooking.setComments(null);

        JsonContent<ItemDtoBooking> result = json.write(itemDtoBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Chairs");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("4 chairs");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathBooleanValue("$.nextBooking.comments").isEqualTo(null);


    }
}