package chilivote.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chilivote.entities.UserEntity;
import chilivote.exceptions.UserNotFoundException;
import chilivote.jwt.JwtTokenUtil;
import chilivote.Repositories.UserRepository;

@Service
public class CommonService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    public UserEntity getOwner(String token){
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        return userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException(user_id));
    }
}