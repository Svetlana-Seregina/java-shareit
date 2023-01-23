package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class UserDto_JsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDto() throws Exception {
        UserDto userDto = new UserDto(
                null,
                "Alex",
                "alex@yandex.ru"
        );

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.name").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.email").isNotBlank();
    }

}