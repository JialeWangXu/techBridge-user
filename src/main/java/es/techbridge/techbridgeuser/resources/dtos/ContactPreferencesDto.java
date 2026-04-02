package es.techbridge.techbridgeuser.resources.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactPreferencesDto {
    private List<String> contactPreferences;
}
