package org.codigo.middleware.mwbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.codigo.middleware.mwbooking.commons.enum_.BookingStatus;
import org.codigo.middleware.mwbooking.utils.AuditableEntity;

import java.time.ZonedDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class_ classEntity;

    @Column(nullable = false)
    private ZonedDateTime bookingTime;

    private ZonedDateTime cancellationTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(nullable = false)
    private boolean isCanceled;
}
