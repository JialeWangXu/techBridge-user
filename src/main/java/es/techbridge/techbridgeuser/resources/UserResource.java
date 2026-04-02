package es.techbridge.techbridgeuser.resources;

import es.techbridge.techbridgeuser.data.entities.ContactPreference;
import es.techbridge.techbridgeuser.data.entities.Province;
import es.techbridge.techbridgeuser.data.entities.User;
import es.techbridge.techbridgeuser.resources.dtos.*;
import es.techbridge.techbridgeuser.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Log4j2
@RestController
@RequestMapping(UserResource.USERS)
public class UserResource {

    private final UserService userService;
    public static final String USERS = "/users";
    public static final String PROVINCES = "/provinces";
    public static final String CONTACTPREFERENCES = "/contactpreferences";

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("permitAll()")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody UserDto userDto){
        userDto.doDefault();
        User user;
        if (userDto instanceof SeniorUserDto seniorDto) {
            user = seniorDto.toSeniorUser();
        } else if (userDto instanceof VolunteerDto volunteerDto) {
            user = volunteerDto.toVolunteer();
        } else {
            throw new IllegalArgumentException("Unknown user type");
        }
        this.userService.create(user);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(PROVINCES)
    public ProvincesDto findProvinces() {
        return new ProvincesDto(Arrays.stream(Province.values())
                .map(Province::name)
                .toList());
    }

    @PreAuthorize("permitAll()")
    @GetMapping(CONTACTPREFERENCES)
    public ContactPreferencesDto findContactPreferences(){
        return new ContactPreferencesDto(Arrays.stream(ContactPreference.values())
                .map(ContactPreference::name)
                .toList());
    }
}
