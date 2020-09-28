package chilivote.controllers;

import java.util.List;

import chilivote.exceptions.BadRequestException;
import chilivote.exceptions.DuplicateEntryException;
import chilivote.exceptions.EntityNotFoundException;
import chilivote.exceptions.UnknownErrorException;
import chilivote.jwt.JwtTokenUtil;
import chilivote.jwt.JwtUserDetailsService;
import chilivote.models.requests.JWTRequest;
import chilivote.models.domain.User;
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

import chilivote.entities.UserEntity;
import chilivote.jwt.JwtResponse;
import chilivote.services.UsersService;
import chilivote.models.domain.UserGenericDTO;
import chilivote.models.requests.FBTokenRequest;
import chilivote.Repositories.FollowRepository;
import chilivote.Repositories.UserRepository;
import org.springframework.http.HttpStatus;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(path="/users")
public class UserController
{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UsersService userLogicHandler;
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
        final UserEntity userDetails = usersRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(() -> new EntityNotFoundException(authenticationRequest.getEmail(), "User"));
        final String accessToken = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(accessToken));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody User user) throws Exception {
        try {
            UserEntity savedUser = userDetailsService.save(user);
            doAuthenticate(user.getEmail(), user.getPassword());
            final UserEntity userDetails = usersRepository.findByEmail(savedUser.getEmail()).orElseThrow(() -> new EntityNotFoundException(user.getEmail(), "User"));
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
    public @ResponseBody Iterable<UserEntity> GetAllUsers()
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