package es.techbridge.techbridgeuser.data.daos;

import es.techbridge.techbridgeuser.data.entities.ContactPreference;
import es.techbridge.techbridgeuser.data.entities.Province;
import es.techbridge.techbridgeuser.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(UUID id);

    @Query("SELECT s.id FROM SeniorUser s WHERE " +
            "(:contactPreference IS NULL OR s.contactPreference = :contactPreference) " +
            "AND (:province IS NULL OR s.province = :province) " +
            "AND (:city IS NULL OR TRIM(:city) = '' OR LOWER(TRIM(s.city)) = LOWER(TRIM(:city)))"
    )
    List<UUID> findSeniorIdsWithFilters(
            @Param("contactPreference") ContactPreference contactPreference,
            @Param("province") Province province,
            @Param("city") String city
    );
}
