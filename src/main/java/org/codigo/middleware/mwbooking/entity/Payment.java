package org.codigo.middleware.mwbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.codigo.middleware.mwbooking.commons.enum_.PaymentStatus;
import org.codigo.middleware.mwbooking.utils.AuditableEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    private ZonedDateTime paymentTime;
}
