package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId()
        );
    }

    public static Item toItem(Long userId, ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(userId);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static Item toUpdateItem(ItemDto itemDtoWithId, ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDtoWithId.getId());
        item.setName(itemDto.getName() != null ? itemDto.getName() : itemDtoWithId.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : itemDtoWithId.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : itemDtoWithId.getAvailable());
        item.setOwnerId(itemDtoWithId.getOwnerId());
        item.setRequestId(itemDtoWithId.getRequestId());
        return item;
    }
}
