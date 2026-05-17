package es.techbridge.techbridgeuser.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFiltersDto {
    private ContactPreference contactPreference;
    private Province province;
    private String city;
}
