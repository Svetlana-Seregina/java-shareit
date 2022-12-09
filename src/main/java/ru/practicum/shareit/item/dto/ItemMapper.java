package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest()
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest())
                .build();
    }

    public static Item toUpdateItem(Long id, ItemDto itemDto) {
        return Item.builder()
                .id(id)
                .name(itemDto.getName() != null ? itemDto.getName() : null)
                .description(itemDto.getDescription() != null ? itemDto.getDescription() : null)
                .available(itemDto.getAvailable())
                .build();
    }
}
