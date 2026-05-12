package es.techbridge.techbridgeuser.resources;

import es.techbridge.techbridgeuser.data.entities.ContactPreference;
import es.techbridge.techbridgeuser.data.entities.Province;
import es.techbridge.techbridgeuser.data.entities.User;
import es.techbridge.techbridgeuser.resources.dtos.*;
import es.techbridge.techbridgeuser.services.UserService;
import es.techbridge.techbridgeuser.services.exceptions.ActivationTokenExpiredException;
import es.techbridge.techbridgeuser.services.exceptions.BadRequestException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    public static final String CONTACT_PREFERENCES = "/contactpreferences";
    public static final String EMAIL = "/email/{email}";
    public static final String ID_ID = "/id/{id}";
    public static final String EMAIL_ID = "/email/{email}/id";
    public static final String ACTIVATE = "/activate";
    public static final String ACTIVATION_TOKEN = "/activation-token";

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
    @GetMapping(ACTIVATE)
    public ModelAndView activate(@RequestParam String token){
        try {
            this.userService.activateAccount(token);
            return activationView(
                    "Cuenta activada",
                    "Cuenta activada correctamente",
                    "Su cuenta ya esta activada. Pulse el boton para ir al inicio de sesion.",
                    false,
                    true,
                    null
            );
        } catch (ActivationTokenExpiredException exception) {
            return activationView(
                    "Enlace caducado",
                    "El enlace ha caducado",
                    "Por seguridad, este enlace ya no se puede usar. Pulse el boton para recibir un nuevo email de activacion.",
                    true,
                    false,
                    token
            );
        } catch (BadRequestException exception) {
            return activationView(
                    "Enlace no valido",
                    "El enlace no se puede usar",
                    "Este enlace no es valido o ya se ha utilizado. Revise si tiene un email de activacion mas reciente.",
                    false,
                    false,
                    null
            );
        }
    }

    @PreAuthorize("permitAll()")
    @PostMapping(ACTIVATION_TOKEN)
    public ModelAndView resendActivationEmail(@RequestParam String token){
        try {
            this.userService.resendActivationEmail(token);
            return activationView(
                    "Email enviado",
                    "Revise su correo",
                    "Le hemos enviado un nuevo enlace para activar su cuenta. El enlace caduca en 24 horas.",
                    false,
                    false,
                    null
            );
        } catch (BadRequestException exception) {
            return activationView(
                    "No se pudo reenviar",
                    "No hemos podido enviar otro enlace",
                    "Este enlace no permite generar un nuevo token. Revise si tiene un email de activacion mas reciente.",
                    false,
                    false,
                    null
            );
        }
    }

    private ModelAndView activationView(String statusLabel,
                                        String title,
                                        String message,
                                        boolean showResendForm,
                                        boolean showLoginLink,
                                        String activationToken) {
        ModelAndView modelAndView = new ModelAndView("activation-result");
        modelAndView.addObject("statusLabel", statusLabel);
        modelAndView.addObject("title", title);
        modelAndView.addObject("message", message);
        modelAndView.addObject("showResendForm", showResendForm);
        modelAndView.addObject("showLoginLink", showLoginLink);
        modelAndView.addObject("activationToken", activationToken);
        return modelAndView;
    }

    @PreAuthorize("permitAll()")
    @GetMapping(PROVINCES)
    public ProvincesDto findProvinces() {
        return new ProvincesDto(Arrays.stream(Province.values())
                .map(Province::name)
                .toList());
    }

    @PreAuthorize("permitAll()")
    @GetMapping(CONTACT_PREFERENCES)
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
