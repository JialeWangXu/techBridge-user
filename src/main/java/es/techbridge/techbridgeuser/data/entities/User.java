package es.techbridge.techbridgeuser.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@Entity
@Table(name = "tb_users") //evitar conflicto con la palabra reservada
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class User extends BaseAuditEntity{
    @Id
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String telephone;
    private String address;
    private String city;

    @Enumerated(EnumType.STRING)
    private Province province;
    private Integer postalCode;
    @Builder.Default
    private Boolean active = true;
}
