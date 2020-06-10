package chilivote.JWT;

import java.util.ArrayList;

import chilivote.Models.Constants.ROLES;
import chilivote.Repositories.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import chilivote.Models.UserDTO;
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
		chilivote.Entities.User user = userRepository.findByEmail(email);
		if(user == null){
			throw new UsernameNotFoundException("The user with the supplied credentials could not be found");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				new ArrayList<>());
    }
    
    public chilivote.Entities.User save(UserDTO user){
        chilivote.Entities.User newUser = new chilivote.Entities.User();
        newUser.setEmail(user.getEmail());
        newUser.setUsername(user.getUsername());
        newUser.setRole(rolesRepository.findByName(ROLES.VIEWER));
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		return userRepository.save(newUser);
    }
}