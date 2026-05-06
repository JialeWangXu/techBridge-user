package es.techbridge.techbridgeuser.services;

import es.techbridge.techbridgeuser.data.daos.UserRepository;
import es.techbridge.techbridgeuser.data.entities.*;
import es.techbridge.techbridgeuser.resources.dtos.SeniorUserDto;
import es.techbridge.techbridgeuser.resources.dtos.UserDto;
import es.techbridge.techbridgeuser.resources.dtos.VolunteerDto;
import es.techbridge.techbridgeuser.services.exceptions.ConflictException;
import es.techbridge.techbridgeuser.services.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

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
                .privacyConsent(true)
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

    @Test
    void getProfile_ShouldReturnSeniorDto_WhenUserIsSenior() {
        UserDto result = userService.getProfile("manolo@gmail.com");
        assertThat(result).isNotNull().isInstanceOf(SeniorUserDto.class);
        assertThat(result.getFirstName()).isEqualTo("Manolo");
    }

    @Test
    void getProfile_ShouldReturnVolunteerDto_WhenUserIsVolunteer() {
        UserDto result = userService.getProfile("lucia@volunteer.org");
        assertThat(result).isNotNull().isInstanceOf(VolunteerDto.class);
        assertThat(result.getFirstName()).isEqualTo("Lucía");
    }

    @Test
    void getProfile_ShouldThrowNotFoundException_WhenUserDoesNotExist() {

        assertThrows(NotFoundException.class, () -> userService.getProfile("testNot@gmail.com"));
    }

    @Test
    void editProfile_ShouldUpdateSeniorCorrectly() {
        SeniorUser updateInfo = new SeniorUser();
        updateInfo.setFirstName("Manolo Editado");
        updateInfo.setContactPreference(ContactPreference.IN_PERSON);
        UserDto result = userService.editProfile("manolo@gmail.com", updateInfo);
        Optional<User> userAfterEdit = userRepository.findByEmail("manolo@gmail.com");
        assertThat(userAfterEdit).isPresent();
        assertThat(userAfterEdit.get().getFirstName()).isEqualTo("Manolo Editado");
        assertThat(((SeniorUserDto)result).getContactPreference()).isEqualTo(ContactPreference.IN_PERSON);
    }

    @Test
    void editProfile_ShouldUpdateVolunteerSpecialties() {

        Volunteer updateInfo = new Volunteer();
        updateInfo.setLastName("García");
        updateInfo.setSpecialties("PC Componentes");
        UserDto result = userService.editProfile("lucia@volunteer.org", updateInfo);
        Optional<User> userAfterEdit = userRepository.findByEmail("lucia@volunteer.org");
        // Assert
        assertThat(result).isInstanceOf(VolunteerDto.class);
        assertThat(result.getLastName()).isEqualTo("García");
        assertThat(userAfterEdit).isPresent();
        assertThat(userAfterEdit.get().getLastName()).isEqualTo("García");
        assertThat(((VolunteerDto)result).getSpecialties()).isEqualTo("PC Componentes");
    }

    @Test
    void getById_Should_ReturnSeniorDto(){
        UserDto result = userService.getProfileById(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertThat(result).isNotNull().isInstanceOf(SeniorUserDto.class);
        assertThat(result.getFirstName()).isEqualTo("Manolo");
    }
}
