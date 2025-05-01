package org.codigo.middleware.mwbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.codigo.middleware.mwbooking.utils.AuditableEntity;

@Entity
@Table(name = "bookings_detail")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDetail extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingDetailId;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "user_package_id", nullable = false)
    private UserPackage userPackage;

    private int creditsDeducted;
}
