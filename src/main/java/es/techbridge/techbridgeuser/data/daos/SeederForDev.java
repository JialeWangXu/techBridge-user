package es.techbridge.techbridgeuser.data.daos;

import es.techbridge.techbridgeuser.data.entities.Province;
import es.techbridge.techbridgeuser.data.entities.SeniorUser;
import es.techbridge.techbridgeuser.data.entities.UserRole;
import es.techbridge.techbridgeuser.data.entities.Volunteer;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Log4j2
@Repository
@Profile({"dev", "test"})
public class SeederForDev {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SeederForDev(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedDatabase();
    }

    private void seedDatabase() {
        log.warn("------- 🎲 Seeding TechBridge Data (Dev Profile) -----------");

        // 1. Limpiamos por si acaso (Ojo: JPA borrará en cascada si está bien configurado)
        this.userRepository.deleteAll();

        String commonPassword = passwordEncoder.encode("password123");

        // 2. Creamos un Senior de prueba
        SeniorUser senior = new SeniorUser();
        senior.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        senior.setFirstName("Manolo");
        senior.setLastName("García");
        senior.setEmail("manolo@gmail.com");
        senior.setPassword(commonPassword);
        senior.setRole(UserRole.SENIOR);
        senior.setProvince(Province.MADRID);
        senior.setDigitalSkillLevel("BASIC"); // Campo específico de Senior
        senior.setContactPreference("PHONE");

        // 3. Creamos un Voluntario de prueba
        Volunteer volunteer = new Volunteer();
        volunteer.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        volunteer.setFirstName("Lucía");
        volunteer.setLastName("Sánchez");
        volunteer.setEmail("lucia@volunteer.org");
        volunteer.setPassword(commonPassword);
        volunteer.setRole(UserRole.VOLUNTEER);
        volunteer.setProvince(Province.BARCELONA);
        volunteer.setSpecialties("Smartphones, WhatsApp"); // Campo específico de Volunteer
        volunteer.setIsAvailable(true);

        // 4. Guardamos ambos usando el repositorio padre
        this.userRepository.saveAll(List.of(senior, volunteer));

        log.warn("------- ✅ Seed Complete: 1 Senior, 1 Volunteer -----------");
    }
}
