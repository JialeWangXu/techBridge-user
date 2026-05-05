package es.techbridge.techbridgeuser.resources.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import es.techbridge.techbridgeuser.data.entities.Province;

import es.techbridge.techbridgeuser.data.entities.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;
import java.util.UUID;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "role",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SeniorUserDto.class, name = "SENIOR"),
        @JsonSubTypes.Type(value = VolunteerDto.class, name = "VOLUNTEER")
})
public abstract class UserDto {
    private UUID id;

    @NotBlank(message = "Firstname is necessary")
    private String firstName;

    @NotBlank(message = "Lastname is necessary")
    private String lastName;

    @Email(message = "The email is not valid")
    @NotBlank(message = "Email is necessary")
    private String email;
    private String password;
    @NotNull(message = "Role is necessary")
    private UserRole role;

    private String telephone;
    private String city;
    private Province province;
    private Integer postalCode;
    private Boolean privacyConsent;

}
