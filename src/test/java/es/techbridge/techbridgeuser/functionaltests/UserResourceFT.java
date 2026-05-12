package es.techbridge.techbridgeuser.functionaltests;

import es.techbridge.techbridgeuser.data.daos.UserRepository;
import es.techbridge.techbridgeuser.data.daos.VerificationTokenRepository;
import es.techbridge.techbridgeuser.data.entities.ContactPreference;
import es.techbridge.techbridgeuser.data.entities.User;
import es.techbridge.techbridgeuser.data.entities.UserRole;
import es.techbridge.techbridgeuser.data.entities.VerificationToken;
import es.techbridge.techbridgeuser.resources.dtos.SeniorUserDto;
import es.techbridge.techbridgeuser.resources.dtos.UserDto;
import es.techbridge.techbridgeuser.resources.dtos.VolunteerDto;
import es.techbridge.techbridgeuser.services.MailService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static es.techbridge.techbridgeuser.resources.UserResource.*;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserResourceFT {
    private final HttpRequestBuilder httpRequestBuilder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @MockitoBean
    private MailService mailService;

    @Autowired
    UserResourceFT(@Value("${spring.security.oauth2.clients.api-client-id}") String apiClientId, @Value("${spring.security.oauth2.clients.api-client-secret}") String apiClientSecret, TestRestTemplate testRestTemplate) {
        this.httpRequestBuilder = HttpRequestBuilder.create(testRestTemplate, apiClientId, apiClientSecret);
    }

    @Test
    void testCreateSeniorUser() {
        SeniorUserDto userDto = SeniorUserDto.builder()
                .contactPreference(ContactPreference.TELEPHONE)
                .firstName("Test1")
                .lastName("Test1")
                .email("test1@gmail.com")
                .password("123")
                .role(UserRole.SENIOR)
                .privacyConsent(true)
                .build();
        ResponseEntity<Void> response = this.httpRequestBuilder
                .post(USERS).body(userDto).exchange(Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testCreateVolunteer() {
        VolunteerDto userDto = VolunteerDto.builder()
                .firstName("Test2")
                .lastName("Test2")
                .email("test2@gmail.com")
                .password("123")
                .role(UserRole.VOLUNTEER)
                .isAvailable(true)
                .specialties("Testing")
                .privacyConsent(true)
                .build();
        ResponseEntity<Void> response = this.httpRequestBuilder
                .post(USERS).body(userDto).exchange(Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testCreateConflict() {
        SeniorUserDto userDto = SeniorUserDto.builder()
                .contactPreference(ContactPreference.TELEPHONE)
                .firstName("Test2")
                .lastName("Test2")
                .email("manolo@gmail.com")
                .password("123")
                .role(UserRole.SENIOR)
                .build();
        ResponseEntity<Void> response = this.httpRequestBuilder
                .post(USERS).body(userDto).exchange(Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testGetProfile() {
        String url = USERS + ME;

        ResponseEntity<SeniorUserDto> response = this.httpRequestBuilder
                .get(url)
                .role(UserRole.SENIOR)
                .exchange(SeniorUserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getFirstName()).isEqualTo("Manolo");
    }

    @Test
    void testEditProfile() {
        SeniorUserDto inputDto = new SeniorUserDto();
        inputDto.setFirstName("Manolo Editado");

        UserDto response = this.httpRequestBuilder
                .put(USERS + ME)
                .role(UserRole.SENIOR)
                .body(inputDto)     // <--- Pasamos el cuerpo
                .exchange(UserDto.class)
                .getBody();

        Assertions.assertNotNull(response);
        assertThat(response.getFirstName()).isEqualTo("Manolo Editado");
    }

    @Test
    void testGetUserByEmail(){
        String url = USERS + EMAIL;

        ResponseEntity<SeniorUserDto> response = this.httpRequestBuilder
                .get(url,"manolo@gmail.com")
                .role(UserRole.SENIOR)
                .exchange(SeniorUserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getFirstName()).isEqualTo("Manolo");
    }

    @Test
    void testGetUserById(){
        String url = USERS + ID_ID;

        ResponseEntity<SeniorUserDto> response = this.httpRequestBuilder
                .get(url, UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .role(UserRole.SENIOR)
                .exchange(SeniorUserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getFirstName()).contains("Manolo");
    }

    @Test
    void testActivateAccount() {
        User user = userRepository.findByEmail("manolo@gmail.com").orElseThrow();
        user.setActive(false);
        userRepository.save(user);
        VerificationToken token = verificationTokenRepository.save(VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .expirationDate(LocalDateTime.now().plusHours(24))
                .used(false)
                .user(user)
                .build());

        ResponseEntity<String> response = this.httpRequestBuilder
                .get(USERS + ACTIVATE)
                .param("token", token.getToken())
                .exchange(String.class);

        Optional<User> activatedUser = userRepository.findByEmail("manolo@gmail.com");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Cuenta activada correctamente");
        assertThat(activatedUser).isPresent();
        assertThat(activatedUser.get().getActive()).isTrue();
    }

    @Test
    void testResendActivationEmail() {
        User user = userRepository.findByEmail("lucia@volunteer.org").orElseThrow();
        user.setActive(false);
        userRepository.save(user);
        VerificationToken expiredToken = verificationTokenRepository.save(VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .expirationDate(LocalDateTime.now().minusMinutes(1))
                .used(false)
                .user(user)
                .build());

        ResponseEntity<String> response = this.httpRequestBuilder
                .post(USERS + ACTIVATION_TOKEN)
                .param("token", expiredToken.getToken())
                .exchange(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Revise su correo");
        assertThat(verificationTokenRepository.existsByUserEmailAndUsedFalse("lucia@volunteer.org")).isTrue();
    }

}
