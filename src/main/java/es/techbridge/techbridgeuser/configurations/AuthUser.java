package es.techbridge.techbridgeuser.configurations;



import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


@Getter
public class AuthUser extends User {
    private final String firstName;

    public AuthUser(String username, String password,
                    Collection<? extends GrantedAuthority> authorities, String firstName, boolean enabled) {
        super(username, password, enabled, true, true, true, authorities);
        this.firstName = firstName;
    }

}
