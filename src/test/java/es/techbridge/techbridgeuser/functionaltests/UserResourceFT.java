package es.techbridge.techbridgeuser.functionaltests;

import es.techbridge.techbridgeuser.data.entities.ContactPreference;
import es.techbridge.techbridgeuser.data.entities.UserRole;
import es.techbridge.techbridgeuser.resources.dtos.SeniorUserDto;
import es.techbridge.techbridgeuser.resources.dtos.UserDto;
import es.techbridge.techbridgeuser.resources.dtos.VolunteerDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static es.techbridge.techbridgeuser.resources.UserResource.ME;
import static es.techbridge.techbridgeuser.resources.UserResource.USERS;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class UserResourceFT {
    private final HttpRequestBuilder httpRequestBuilder;

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

        assertThat(response.getFirstName()).isEqualTo("Manolo Editado");
    }

}
