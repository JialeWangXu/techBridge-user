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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping(UserResource.USERS)
public class UserResource {

    private final UserService userService;
    public static final String USERS = "/users";
    public static final String PROVINCES = "/provinces";
    public static final String ME = "/me";
    public static final String CONTACTPREFERENCES = "/contactpreferences";
    public static final String EMAIL = "/email/{email}";
    public static final String ID_ID = "/id/{id}";
    public static final String EMAIL_ID = "/email/{email}/id";

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("permitAll()")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody UserDto userDto){
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

    @GetMapping(ME)
    public UserDto getMyProfile(@AuthenticationPrincipal Jwt jwt){
        String email = jwt.getSubject();
        return this.userService.getProfile(email);
    }

    @PutMapping(ME)
    public UserDto editMyProfile(@AuthenticationPrincipal Jwt jwt,@RequestBody UserDto user){
        if (user instanceof SeniorUserDto seniorDto) {
            return this.userService.editProfile(jwt.getSubject(), seniorDto.toSeniorUser());
        } else if (user instanceof VolunteerDto volunteerDto) {
            return this.userService.editProfile(jwt.getSubject(), volunteerDto.toVolunteer());
        } else {
            throw new IllegalArgumentException("Unknown user type");
        }
    }

    @GetMapping(EMAIL)
    public UserDto getByEmail(@PathVariable String email){
        return this.userService.getProfile(email);
    }

    @GetMapping(ID_ID)
    public UserDto getById(@PathVariable UUID id){
        return this.userService.getProfileById(id);
    }

    @GetMapping(EMAIL_ID)
    public UUID getIdByEmail(@PathVariable String email){
        return this.userService.getProfile(email).getId();
    }

}
