package es.techbridge.techbridgeuser.resources.dtos;

import es.techbridge.techbridgeuser.data.entities.Volunteer;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VolunteerDto extends UserDto{
    private String specialties;
    private Boolean isAvailable;

    public VolunteerDto(Volunteer volunteer){
        BeanUtils.copyProperties(volunteer, this);
        this.setPassword(null);
    }

    public Volunteer toVolunteer(){
        return Volunteer.builder()
                .specialties(this.getSpecialties())
                .isAvailable(this.getIsAvailable())
                .id(this.getId())
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .email(this.getEmail())
                .password(this.getPassword())
                .role(this.getRole())
                .telephone(this.getTelephone())
                .address(this.getAddress())
                .city(this.getCity())
                .province(this.getProvince())
                .postalCode(this.getPostalCode())
                .active(this.getActive())
                .build();
    }
}
