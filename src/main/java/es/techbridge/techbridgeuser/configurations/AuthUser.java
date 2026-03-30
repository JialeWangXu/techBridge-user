package es.techbridge.techbridgeuser.configurations;



import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


@Getter
public class AuthUser extends User {
    private final String firstName;

    public AuthUser(String username, String password,
                    Collection<? extends GrantedAuthority> authorities, String firstName) {
        super(username, password, authorities);
        this.firstName = firstName;
    }

}
