package es.techbridge.techbridgeuser.services;

import es.techbridge.techbridgeuser.data.daos.UserRepository;
import es.techbridge.techbridgeuser.data.daos.VerificationTokenRepository;
import es.techbridge.techbridgeuser.data.entities.*;
import es.techbridge.techbridgeuser.resources.dtos.SeniorUserDto;
import es.techbridge.techbridgeuser.resources.dtos.UserDto;
import es.techbridge.techbridgeuser.resources.dtos.VolunteerDto;
import es.techbridge.techbridgeuser.services.exceptions.ActivationTokenExpiredException;
import es.techbridge.techbridgeuser.services.exceptions.BadRequestException;
import es.techbridge.techbridgeuser.services.exceptions.ConflictException;
import es.techbridge.techbridgeuser.services.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final String activationUrl;

    @Autowired
    public UserService(UserRepository userRepository,
                       VerificationTokenRepository verificationTokenRepository,
                       PasswordEncoder passwordEncoder,
                       MailService mailService,
                       @Value("${app.activation-url:http://localhost:8081/users/activate}") String activationUrl){
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.activationUrl = activationUrl;
    }

    @Transactional
    public void create(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new ConflictException("The email already exists");
        }
        user.setId(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(Boolean.TRUE.equals(user.getPrivacyConsent())) user.setPrivacyConsentTime(LocalDateTime.now());
        user.setActive(false);
        User savedUser = this.userRepository.save(user);
        sendActivationEmail(savedUser);
    }

    @Transactional
    public void activateAccount(String tokenValue){
        VerificationToken token = verificationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BadRequestException("Invalid activation token"));

        if (Boolean.TRUE.equals(token.getUsed())) {
            throw new BadRequestException("Activation token has already been used");
        }
        if (token.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ActivationTokenExpiredException();
        }

        User user = token.getUser();
        user.setActive(true);
        token.setUsed(true);
        userRepository.save(user);
        verificationTokenRepository.save(token);
    }

    @Transactional
    public void resendActivationEmail(String expiredTokenValue){
        VerificationToken expiredToken = verificationTokenRepository.findByToken(expiredTokenValue)
                .orElseThrow(() -> new BadRequestException("Invalid activation token"));

        if (Boolean.TRUE.equals(expiredToken.getUsed())) {
            throw new BadRequestException("Activation token has already been used");
        }
        if (!expiredToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Activation token has not expired");
        }

        User user = expiredToken.getUser();
        if (Boolean.TRUE.equals(user.getActive())) {
            throw new BadRequestException("The account is already active");
        }
        verificationTokenRepository.delete(expiredToken);
        verificationTokenRepository.flush();
        sendActivationEmail(user);
    }

    private void sendActivationEmail(User user) {
        verificationTokenRepository.deleteByUserAndUsedFalse(user);
        String tokenValue = UUID.randomUUID().toString();
        VerificationToken token = VerificationToken.builder()
                .token(tokenValue)
                .expirationDate(LocalDateTime.now().plusMinutes(2))
                .used(false)
                .user(user)
                .build();
        verificationTokenRepository.save(token);

        String separator = activationUrl.contains("?") ? "&" : "?";
        String activationLink = activationUrl + separator + "token=" + tokenValue;
        mailService.sendActivationEmail(user.getEmail(), activationLink);
    }

    public UserDto getProfile(String email){
        User user = findByEmail(email);
        if(user.getRole()== UserRole.SENIOR){
            return new SeniorUserDto((SeniorUser) user);
        }else{
            return new VolunteerDto((Volunteer) user);
        }
    }

    public UserDto getProfileById(UUID id){
        Optional<User> user = this.userRepository.findById(id);
        if(user.isPresent()){
            if(user.get().getRole()== UserRole.SENIOR){
                return new SeniorUserDto((SeniorUser) user.get());
            }else{
                return new VolunteerDto((Volunteer) user.get());
            }
        }else{
            throw new NotFoundException("No user exists in the system with id: "+id);
        }

    }

    public UserDto editProfile(String email, User userInfo){
        User user = findByEmail(email);
        user = updateBasicProfile(user,userInfo);
        if(user.getRole()==UserRole.SENIOR){
            return new SeniorUserDto(updateSeniorProfile((SeniorUser) user,(SeniorUser) userInfo));
        }else{
            return new VolunteerDto(updateVolunteerProfile((Volunteer) user, (Volunteer) userInfo));
        }
    }

    private User findByEmail(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(!user.isPresent()){
            throw new NotFoundException("No user exists in the system with email: "+email);
        }else{
            return user.get();
        }
    }

    private User updateBasicProfile(User existing, User updateInfo ){
        if (updateInfo.getFirstName() !=null) existing.setFirstName(updateInfo.getFirstName());
        if (updateInfo.getLastName()!=null) existing.setLastName(updateInfo.getLastName());
        if (updateInfo.getTelephone()!=null) existing.setTelephone(updateInfo.getTelephone());
        if (updateInfo.getCity()!=null) existing.setCity(updateInfo.getCity());
        if (updateInfo.getProvince()!=null) existing.setProvince(updateInfo.getProvince());
        if(updateInfo.getPostalCode()!=null) existing.setPostalCode(updateInfo.getPostalCode());
        return userRepository.save(existing);
    }

    private SeniorUser updateSeniorProfile(SeniorUser existing, SeniorUser updateInfo){
        if(updateInfo.getContactPreference()!=null){
            existing.setContactPreference(updateInfo.getContactPreference());
            return userRepository.save(existing);
        }else{
            return existing;
        }
    }

    private Volunteer updateVolunteerProfile(Volunteer existing, Volunteer updateInfo){
        if(updateInfo.getIsAvailable()==null && updateInfo.getSpecialties() ==null){
            return existing;
        }else{
            if(updateInfo.getSpecialties()!=null) existing.setSpecialties(updateInfo.getSpecialties());
            if(updateInfo.getIsAvailable()!=null) existing.setIsAvailable(updateInfo.getIsAvailable());
            return userRepository.save(existing);
        }
    }

    public List<UUID> getFilteredUserIds(UserFiltersDto filtersDto){
        return this.userRepository.findSeniorIdsWithFilters(filtersDto.getContactPreference()
                ,filtersDto.getProvince(),
                filtersDto.getCity());
    }

}
