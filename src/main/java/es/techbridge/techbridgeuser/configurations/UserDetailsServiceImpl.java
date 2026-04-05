package es.techbridge.techbridgeuser.configurations;

import es.techbridge.techbridgeuser.data.daos.UserRepository;
import es.techbridge.techbridgeuser.data.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        // Convertir el Rol de la BD en una "Authority" que Spring entienda --> empieza con ROLE_
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase())
        );

        return new AuthUser(
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getFirstName()
        );
    }
}
