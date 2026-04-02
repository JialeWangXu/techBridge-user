package es.techbridge.techbridgeuser.resources.dtos;

import es.techbridge.techbridgeuser.data.entities.ContactPreference;
import es.techbridge.techbridgeuser.data.entities.SeniorUser;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeniorUserDto extends UserDto{

    private ContactPreference contactPreference;

    public SeniorUserDto(SeniorUser seniorUser){
        BeanUtils.copyProperties(seniorUser, this);
        this.setPassword(null);
    }

    public SeniorUser toSeniorUser(){
        return SeniorUser.builder()
                .contactPreference(this.contactPreference)
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
