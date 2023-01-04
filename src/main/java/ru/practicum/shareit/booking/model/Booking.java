package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@ToString
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FutureOrPresent
    @Column(name = "start_date")
    private LocalDateTime start;

    @Future
    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booker_id")
    private User booker;

    @Transient
    private Long bookerId;

    @Enumerated(EnumType.STRING)
    private BookingState status;

    // WAITING - новое бронирование, ожидает одобрения
    // APPROVED - бронирование подтверждено владельцем
    // REJECTED - бронирование отклонено владельцем
    // CANCELED - бронирование отменено создателем


}
