package es.techbridge.techbridgeuser.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "TechBridge-User", version = "v1"),
        security = @SecurityRequirement(name = "oauth2")
        // cualquier endpoint de esta api hay que autenticarse siguiendo el protocolo oauth2
)
@SecurityScheme(
        name = "oauth2",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "http://localhost:8081/oauth2/authorize", //pantalla de login
                        tokenUrl = "http://localhost:8081/oauth2/token",
                        scopes = {
                                @OAuthScope(name = "profile", description = "profile")
                        }
                )
        )
)
@Configuration
public class OpenApiConfig {
    // empty
}
