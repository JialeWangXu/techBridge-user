package es.techbridge.techbridgeuser.data.entities;

public enum UserRole {
    SENIOR,
    VOLUNTEER;

    public static UserRole from(String authority) {
        return UserRole.valueOf(authority.replace("ROLE_", ""));
    }

    public String jwtClaimValue() {
        return this.name().toLowerCase();
    }
}
