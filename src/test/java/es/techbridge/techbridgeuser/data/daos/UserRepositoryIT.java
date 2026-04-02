package es.techbridge.techbridgeuser.data.daos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByMobile() {
        assertThat(this.userRepository.findByEmail("manolo@gmail.com")).isPresent();
    }

    @Test
    void testFindByScopeIn() {
        assertThat(this.userRepository.existsByEmail("manolo@gmail.com")).isTrue();
    }
}
