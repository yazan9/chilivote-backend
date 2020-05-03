package chilivote.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chilivote.Entities.User;
import chilivote.Exceptions.UserNotFoundException;
import chilivote.JWT.JwtTokenUtil;
import chilivote.Repositories.UserRepository;

@Service
public class CommonService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    public User getOwner(String token){
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        return userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException(user_id));
    }
}