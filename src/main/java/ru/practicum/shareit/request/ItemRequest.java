package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequest {

    private final Long id;
    private final String description;
    private final User requestor;
    @PastOrPresent
    private final LocalDate created;

}
