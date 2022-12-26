package ru.practicum.shareit.booking.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", schema = "public")
@Getter @Setter @ToString
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@FutureOrPresent
    @Column(name = "start_date")
    private LocalDateTime start;

    //@Future
    @Column(name = "end_date")
    private LocalDateTime end;

    private Long itemId;

    private Long bookerId;

    @Enumerated(EnumType.STRING)
    private BookingState status;
    // WAITING - новое бронирование, ожидает одобрения
    // APPROVED - бронирование подтверждено владельцем
    // REJECTED - бронирование отклонено владельцем
    // CANCELED - бронирование отменено создателем


}
