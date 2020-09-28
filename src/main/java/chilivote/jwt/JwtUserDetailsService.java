package chilivote.jwt;

import java.util.ArrayList;

import chilivote.entities.UserEntity;
import chilivote.exceptions.EntityNotFoundException;
import chilivote.models.constants.ROLES;
import chilivote.Repositories.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import chilivote.models.domain.User;
import chilivote.Repositories.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
	private UserRepository userRepository;

    @Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
		if(user == null){
			throw new UsernameNotFoundException("The user with the supplied credentials could not be found");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword() == null ? "dummyPassword" : user.getPassword(),
				new ArrayList<>());
    }
    
    public UserEntity save(User user){
        UserEntity newUser = new UserEntity();
        newUser.setEmail(user.getEmail());
        newUser.setUsername(user.getUsername());
        newUser.setRole(rolesRepository.findByName(ROLES.VIEWER).orElseThrow(() -> new EntityNotFoundException(ROLES.VIEWER, "Role")));
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		return userRepository.save(newUser);
    }
}