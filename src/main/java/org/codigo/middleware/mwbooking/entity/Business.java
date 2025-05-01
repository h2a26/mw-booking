package org.codigo.middleware.mwbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.codigo.middleware.mwbooking.utils.AuditableEntity;

@Entity
@Table(name = "business")
@Getter
@Setter
public class Business extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long businessId;

    private String businessName;
    private String country;
}
