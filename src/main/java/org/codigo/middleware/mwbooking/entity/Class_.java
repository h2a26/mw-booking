package org.codigo.middleware.mwbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.codigo.middleware.mwbooking.utils.AuditableEntity;

import java.time.ZonedDateTime;

@Entity
@Table(name = "classes")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Class_ extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classId;

    private String className;
    private String country;

    private int requiredCredits;
    private int availableSlots;

    @Column(nullable = false)
    private ZonedDateTime classStartDate;

    @Column(nullable = false)
    private ZonedDateTime classEndDate;

    @ManyToOne
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;
}
