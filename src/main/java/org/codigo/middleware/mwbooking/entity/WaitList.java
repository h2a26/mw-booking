package org.codigo.middleware.mwbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.codigo.middleware.mwbooking.utils.AuditableEntity;

@Entity
@Table(name = "waitlists")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaitList extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long waitlistId;
    private int waitlistPosition;

    @OneToOne
    @JoinColumn(name = "booking_id", unique = true)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class_ clazz;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
