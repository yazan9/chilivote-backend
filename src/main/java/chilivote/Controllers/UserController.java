package chilivote.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import chilivote.Entities.User;
import chilivote.JWT.JwtResponse;
import chilivote.JWT.JwtTokenUtil;
import chilivote.LogicHandlers.UserLogicHandler;
import chilivote.Models.Requests.FBTokenRequest;
import chilivote.Repositories.UserRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(path="/users")
public class UserController
{
    @Autowired //for repository auto-generation
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @PostMapping(path="/login")
    public @ResponseBody ResponseEntity<?> Login(@RequestBody FBTokenRequest FBToken) throws Exception
    {
        return ResponseEntity.ok(new JwtResponse(new UserLogicHandler(userRepository, jwtTokenUtil).getJWTToken(FBToken.FBToken)));
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> GetAllUsers()
    {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody User one(@PathVariable Integer id) {
        //return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return null;
    }
}