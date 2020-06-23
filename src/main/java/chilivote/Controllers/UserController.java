package chilivote.Controllers;

import java.util.List;

import chilivote.Exceptions.BadRequestException;
import chilivote.Exceptions.DuplicateEntryException;
import chilivote.Exceptions.UnknownErrorException;
import chilivote.JWT.JwtTokenUtil;
import chilivote.JWT.JwtUserDetailsService;
import chilivote.Models.Requests.JWTRequest;
import chilivote.Models.UserDTO;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private UserRepository usersRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @PostMapping(path="/login")
    public @ResponseBody ResponseEntity<?> Login(@RequestBody FBTokenRequest FBToken) throws Exception
    {
        return ResponseEntity.ok(new JwtResponse(userLogicHandler.getJWTToken(FBToken.FBToken)));
    }

    @PostMapping(path="/emailLogin")
    public @ResponseBody ResponseEntity<?> EmailLogin(@RequestBody JWTRequest authenticationRequest) throws Exception
    {
        doAuthenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        final User userDetails = usersRepository.findByEmail(authenticationRequest.getEmail());
        final String accessToken = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(accessToken));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody UserDTO user) throws Exception {
        try {
            User savedUser = userDetailsService.save(user);
            doAuthenticate(user.getEmail(), user.getPassword());
            final User userDetails = usersRepository.findByEmail(savedUser.getEmail());
            final String accessToken = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponse(accessToken));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException("A user with the same email already exists");
        } catch (ConstraintViolationException e) {
            throw new BadRequestException(
                    "Could not register the user. Please make sure that all required fields are present");
        } catch (Exception e) {
            throw new UnknownErrorException();
        }
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

    private void doAuthenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
        catch(Exception e){
            throw new Exception("Internal error");
        }
    }
}