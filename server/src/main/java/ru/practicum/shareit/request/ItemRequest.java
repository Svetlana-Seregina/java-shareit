package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requestor_id")
    private User requestor;

    private LocalDateTime created;

}
