package chilivote.LogicHandlers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import chilivote.Entities.User;
import chilivote.JWT.JwtTokenUtil;
import chilivote.Repositories.UserRepository;

@Component
public class AuthorizationLogicHandler{

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserRepository userRepository;

    public boolean checkPermissions(List<String> scope, String token){
        String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        User user = userRepository.findByEmail(email);

        List<String> loadedPermissions = user.getPermissions().stream().map(permission -> permission.getPermission().getName()).collect(Collectors.toList()); 
        return loadedPermissions == null ? false : loadedPermissions.containsAll(scope);
    }
}