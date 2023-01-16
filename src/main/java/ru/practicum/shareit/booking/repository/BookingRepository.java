package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> getAllByBooker_Id(Long userId, Sort sort);

    List<Booking> getAllByBooker_IdAndStartIsAfter(Long userId, LocalDateTime localDateTime, Sort sort);

    List<Booking> getAllByBooker_IdAndStatusAndStartIsAfter(Long userId, BookingState state, LocalDateTime localDateTime);

    List<Booking> getAllByBooker_IdAndStartBeforeAndEndAfter(Long userId, LocalDateTime ldt1, LocalDateTime ltd2);

    List<Booking> getAllByBooker_IdAndStartBeforeAndEndBefore(Long userId, LocalDateTime ldt1, LocalDateTime ldt2);

    List<Booking> getAllByItem_OwnerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime ldt1, LocalDateTime ldt2);

    List<Booking> getAllByItem_OwnerIdAndStartBeforeAndEndBefore(Long userId, LocalDateTime ldt1, LocalDateTime ldt2);

    Page<Booking> getAllByItem_OwnerId(Long userId, Pageable pageable);

    List<Booking> getAllByItem_OwnerIdAndStartIsAfter(Long userId, LocalDateTime localDateTime, Sort sort);

    List<Booking> getAllByItem_OwnerIdAndStatusAndStartIsAfter(Long id, BookingState state, LocalDateTime localDateTime);

    List<Booking> getAllByItem_IdAndStartIsLessThanEqualOrEndIsLessThanEqualAndStatusIs(
            Long id, LocalDateTime ldt1, LocalDateTime ldt2, BookingState bs);

    List<Booking> getAllByItem_IdAndStartIsAfterAndStatusIs(Long id, LocalDateTime localDateTime, BookingState bs);

    List<Booking> getAllByItem_IdAndStartIsBeforeAndEndIsBefore(Long id, LocalDateTime ldt1, LocalDateTime ldt2);

    List<Booking> getAllByItem_InAndStartIsLessThanEqualOrEndIsLessThanEqualAndStatusIs(
            List<Item> items, LocalDateTime ldt1, LocalDateTime ldt2, BookingState bs);

    List<Booking> getAllByItem_InAndStartAfterAndStatusIs(List<Item> items, LocalDateTime ldt, BookingState bs);


}
