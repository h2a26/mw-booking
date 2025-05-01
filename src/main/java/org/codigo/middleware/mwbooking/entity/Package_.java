package org.codigo.middleware.mwbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.codigo.middleware.mwbooking.utils.AuditableEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "packages")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Package_ extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;

    private String packageName;
    private int totalCredits;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private int expiryDays;
    private String country;
}
