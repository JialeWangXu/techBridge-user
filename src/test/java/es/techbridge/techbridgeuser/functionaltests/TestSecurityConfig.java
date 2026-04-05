package es.techbridge.techbridgeuser.functionaltests;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary // Esto hace que Spring use este decoder en lugar del real
    public JwtDecoder jwtDecoder() {
        // Esta es la "magia": interceptamos el token que viene del servidor
        // y le cambiamos el 'sub' por el email que necesitamos para la DB.
        return token -> {
            // Aquí puedes usar un decoder real o simplemente crear uno falso
            return Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .claim("sub", "manolo@gmail.com") // <--- Forzamos el email de Manolo
                    .claim("scope", "profile")
                    .claim("role", "ROLE_SENIOR")
                    .build();
        };
    }
}
