package es.techbridge.techbridgeuser.data.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Table(name = "tb_senior_users")
@PrimaryKeyJoinColumn(name = "user_id") // El ID en esta tabla se llamará user_id y será FK a users
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SeniorUser extends User {

    private String contactPreference;

    private String digitalSkillLevel;

}
