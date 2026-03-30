package es.techbridge.techbridgeuser.configurations;

import java.util.Arrays;
import java.util.List;

public enum OAuth2Scope {
    OPENID, PROFILE, OFFLINE_ACCESS;

    public static final String PREFIX = "SCOPE_";

    public static List<String> allJwtClaimValues() {
        return Arrays.stream(OAuth2Scope.values())
                .map(OAuth2Scope::jwtClaimValue)
                .toList();
    }

    public static OAuth2Scope from(String withPrefix) {
        return OAuth2Scope.valueOf(withPrefix
                .replace(PREFIX, "")
                .toUpperCase());
    }

    public String springSecurityAuthority() {
        return PREFIX + this.jwtClaimValue();
    }

    public String jwtClaimValue() {
        return this.name().toLowerCase();
    }

}
