package es.techbridge.techbridgeuser.configurations;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {
    public static final String PREFIX = "ROLE_";
    public static final String CLAIM_NAME = "roles";
    public static final String AWS_CLAIM_NAME = "cognito:groups";

    @Bean
    @Order(1)
    public SecurityFilterChain apiJwt(HttpSecurity http) throws Exception{
        return http.securityMatcher("/users/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers(HttpMethod.POST,"/users").permitAll()
                                .requestMatchers(HttpMethod.GET,"/users/provinces").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())
                ))
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        // para convertir en objetos que Spring Security entienda (los GrantedAuthority o Roles)
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // Spring Security necesita que sea ROLE_xxx para que funcionen anotaciones como @PreAuthorize
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(CLAIM_NAME);

        return getJwtAuthenticationConverter(jwtGrantedAuthoritiesConverter);
    }

    private static @NonNull JwtAuthenticationConverter getJwtAuthenticationConverter(JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter( jwt -> {
            if (jwt.getClaim(CLAIM_NAME)!=null) {
                return jwtGrantedAuthoritiesConverter.convert(jwt);
            }else{
                // si el token viene de AWS Cognito.
                return Optional.ofNullable(jwt.getClaimAsStringList(AWS_CLAIM_NAME))
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(group -> new SimpleGrantedAuthority(PREFIX + group))
                        .collect(Collectors.toList());
            }
        });
        return jwtAuthenticationConverter;
    }
}
