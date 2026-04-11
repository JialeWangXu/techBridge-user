package es.techbridge.techbridgeuser.services;

import es.techbridge.techbridgeuser.data.daos.UserRepository;
import es.techbridge.techbridgeuser.data.entities.SeniorUser;
import es.techbridge.techbridgeuser.data.entities.User;
import es.techbridge.techbridgeuser.data.entities.UserRole;
import es.techbridge.techbridgeuser.data.entities.Volunteer;
import es.techbridge.techbridgeuser.resources.dtos.SeniorUserDto;
import es.techbridge.techbridgeuser.resources.dtos.UserDto;
import es.techbridge.techbridgeuser.resources.dtos.VolunteerDto;
import es.techbridge.techbridgeuser.services.exceptions.ConflictException;
import es.techbridge.techbridgeuser.services.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void create(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new ConflictException("The email already exists");
        }
        user.setId(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.userRepository.save(user);
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
        if (updateInfo.getAddress()!=null) existing.setAddress(updateInfo.getAddress());
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

}
