package es.techbridge.techbridgeuser.services;

import es.techbridge.techbridgeuser.data.daos.UserRepository;
import es.techbridge.techbridgeuser.data.entities.ContactPreference;
import es.techbridge.techbridgeuser.data.entities.SeniorUser;
import es.techbridge.techbridgeuser.data.entities.User;
import es.techbridge.techbridgeuser.data.entities.UserRole;
import es.techbridge.techbridgeuser.services.exceptions.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @Test
    void testCreate() {
        User user = SeniorUser.builder()
                .contactPreference(ContactPreference.TELEPHONE)
                .firstName("Test1")
                .lastName("Test1")
                .email("test1@gmail.com")
                .password("123")
                .role(UserRole.SENIOR)
                .build();
        this.userService.create(user);
        assertThat(this.userRepository.existsByEmail("test1@gmail.com")).isTrue();
    }

    @Test
    void testCreateForbidden() {
        User user = SeniorUser.builder()
                .contactPreference(ContactPreference.TELEPHONE)
                .firstName("Test2")
                .lastName("Test2")
                .email("manolo@gmail.com")
                .password("123")
                .role(UserRole.SENIOR)
                .build();
        assertThrows(ConflictException.class, () -> this.userService.create(user));
    }
}
