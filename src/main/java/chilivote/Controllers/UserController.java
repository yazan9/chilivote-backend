package chilivote.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import chilivote.Entities.User;
import chilivote.JWT.JwtResponse;
import chilivote.LogicHandlers.UserLogicHandler;
import chilivote.Models.DTOs.UserGenericDTO;
import chilivote.Models.Requests.FBTokenRequest;
import chilivote.Repositories.FollowRepository;
import chilivote.Repositories.UserRepository;
import org.springframework.http.HttpStatus;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(path="/users")
public class UserController
{
    @Autowired //for repository auto-generation
    private UserRepository userRepository;
    @Autowired //for repository auto-generation
    private FollowRepository followRepository;
    @Autowired
    private UserLogicHandler userLogicHandler;
    
    @PostMapping(path="/login")
    public @ResponseBody ResponseEntity<?> Login(@RequestBody FBTokenRequest FBToken) throws Exception
    {
        return ResponseEntity.ok(new JwtResponse(userLogicHandler.getJWTToken(FBToken.FBToken)));
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> GetAllUsers()
    {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public @ResponseBody UserGenericDTO one(@PathVariable Integer id) {
        return userLogicHandler.getUserGenericInformation(id);
    }

    @GetMapping(path="/followers")
    public @ResponseBody List<UserGenericDTO> followers(@RequestHeader("Authorization") String token)
    {
        return userLogicHandler.getFollowers(token);
    }

    @GetMapping(path="/following")
    public @ResponseBody List<UserGenericDTO> following(@RequestHeader("Authorization") String token)
    {
        return userLogicHandler.getFollowing(token);
    }

    @GetMapping(path="/search")
    public @ResponseBody List<UserGenericDTO> search(@RequestHeader("Authorization") String token, @RequestParam String q)
    {
        return userLogicHandler.search(token, q);
    }

    @GetMapping(path="/suggested_users")
    public @ResponseBody List<UserGenericDTO> suggestedUsers(@RequestHeader("Authorization") String token)
    {
        return userLogicHandler.getRandomUsers(token);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path="/follow/{id}")
    public @ResponseBody void follow(@RequestHeader("Authorization") String token, @PathVariable Integer id)
    {
        userLogicHandler.follow(id, token, followRepository);
    } 

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path="/unfollow/{id}")
    public @ResponseBody void unfollow(@RequestHeader("Authorization") String token, @PathVariable Integer id)
    {
        userLogicHandler.unfollow(id, token, followRepository);
    } 

    @GetMapping(path="/get_role")
    public @ResponseBody String checkRole(@RequestHeader("Authorization") String token)
    {
        return userLogicHandler.getRole(token);
    }

    @PostMapping(path="/hide/{id}")
    public ResponseEntity<?> hide(@RequestHeader("Authorization") String token, @PathVariable Integer id){
        return userLogicHandler.hideUser(token, id);
    }
}