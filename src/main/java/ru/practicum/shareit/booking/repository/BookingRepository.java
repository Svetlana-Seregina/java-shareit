package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> getAllByBooker_Id(Long userId);

    List<Booking> getAllByBooker_IdAndStartIsAfter(Long userId, LocalDateTime localDateTime);

    List<Booking> getAllByBooker_IdAndStatusAndStartIsAfter(Long userId, BookingState state, LocalDateTime localDateTime);

    List<Booking> getAllByBooker_IdAndStartBeforeAndEndAfter(Long userId, LocalDateTime ldt1, LocalDateTime ltd2);

    List<Booking> getAllByBooker_IdAndStartBeforeAndEndBefore(Long userId, LocalDateTime ldt1, LocalDateTime ldt2);

    List<Booking> getAllByItem_OwnerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime ldt1, LocalDateTime ldt2);

    List<Booking> getAllByItem_OwnerIdAndStartBeforeAndEndBefore(Long userId, LocalDateTime ldt1, LocalDateTime ldt2);

    List<Booking> getAllByItem_OwnerId(Long userId);

    List<Booking> getAllByItem_OwnerIdAndStartIsAfter(Long userId, LocalDateTime localDateTime);

    List<Booking> getAllByItem_OwnerIdAndStatusAndStartIsAfter(Long id, BookingState state, LocalDateTime localDateTime);

    Optional<Booking> getAllByItem_IdAndStartIsBefore(Long id, LocalDateTime localDateTime);

    Optional<Booking> getAllByItem_IdAndStartIsAfter(Long id, LocalDateTime localDateTime);

    List<Booking> getAllByItem_IdAndStartIsBeforeAndEndIsBefore(Long id, LocalDateTime ldt1, LocalDateTime ldt2);



}
