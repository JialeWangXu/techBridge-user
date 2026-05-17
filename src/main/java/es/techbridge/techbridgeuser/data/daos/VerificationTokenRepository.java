package es.techbridge.techbridgeuser.data.daos;

import es.techbridge.techbridgeuser.data.entities.User;
import es.techbridge.techbridgeuser.data.entities.VerificationToken;
import es.techbridge.techbridgeuser.data.entities.VerificationTokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByTokenAndTokenType(String token, VerificationTokenType tokenType);

    boolean existsByUserEmailAndUsedFalse(String email);

    boolean existsByUserEmailAndUsedFalseAndTokenType(String email, VerificationTokenType tokenType);

    void deleteByUserAndUsedFalseAndTokenType(User user, VerificationTokenType tokenType);
}
