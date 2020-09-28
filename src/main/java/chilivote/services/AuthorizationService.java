package chilivote.services;

import java.util.List;
import java.util.stream.Collectors;

import chilivote.entities.PermissionEntity;
import chilivote.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import chilivote.entities.UserEntity;
import chilivote.jwt.JwtTokenUtil;
import chilivote.Repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserRepository userRepository;

    public boolean checkPermissions(List<String> scope, String token){
        String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(email, "User"));

        List<String> loadedPermissions = user.getRole().getPermissions().stream().map(PermissionEntity::getName).collect(Collectors.toList());
        return loadedPermissions.containsAll(scope);
    }
}