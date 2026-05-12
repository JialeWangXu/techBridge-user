package es.techbridge.techbridgeuser.data.daos;

import es.techbridge.techbridgeuser.data.entities.User;
import es.techbridge.techbridgeuser.data.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByToken(String token);

    boolean existsByUserEmailAndUsedFalse(String email);

    void deleteByUserAndUsedFalse(User user);
}
