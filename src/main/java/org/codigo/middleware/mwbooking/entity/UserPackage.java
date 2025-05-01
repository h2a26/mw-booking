package org.codigo.middleware.mwbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.codigo.middleware.mwbooking.commons.enum_.PackageStatus;
import org.codigo.middleware.mwbooking.utils.AuditableEntity;

import java.time.ZonedDateTime;

@Entity
@Table(name = "user_packages")
@Getter
@Setter
public class UserPackage extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPackageId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private Package packageEntity;

    private int remainingCredits;

    @Enumerated(EnumType.STRING)
    private PackageStatus status;

    private ZonedDateTime expirationDate;
}
