package chilivote.LogicHandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import chilivote.Entities.Follow;
import chilivote.Entities.User;
import chilivote.Exceptions.DuplicateRelationshipEntryException;
import chilivote.Exceptions.RelationshipNotFoundException;
import chilivote.Exceptions.UnknownErrorException;
import chilivote.Exceptions.UserNotFoundException;
import chilivote.JWT.JwtTokenUtil;
import chilivote.Models.FacebookProfile;
import chilivote.Models.DTOs.FollowerDTO;
import chilivote.Models.DTOs.FollowingDTO;
import chilivote.Models.DTOs.UserGenericDTO;
import chilivote.Models.DTOs.UserMeDTO;
import chilivote.Repositories.FollowRepository;
import chilivote.Repositories.UserRepository;

public class UserLogicHandler
{
    private UserRepository userRepository;
    private JwtTokenUtil jwtTokenUtil;

    public UserLogicHandler(UserRepository userRepository, JwtTokenUtil jwtTokenUtil)
    {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public String getJWTToken(String FBToken) throws Exception
    {
        FacebookProfile facebookProfile = new ObjectMapper().readValue(getFBProfileFromToken(FBToken), FacebookProfile.class);     
        User userDetails = userRepository.findByEmail(facebookProfile.email);
        if(userDetails == null)
            return CreateNewUser(facebookProfile);
        final String token = jwtTokenUtil.generateToken(userDetails);
        return token;
    }

    protected String CreateNewUser(FacebookProfile facebookProfile)
    {
        User SavedUser = userRepository.save(ToUserEntity(facebookProfile));
        return jwtTokenUtil.generateToken(SavedUser);
    }

    protected String getFBProfileFromToken(String FBToken) throws Exception
    {
        RestTemplate restTemplate = new RestTemplate();
        String facebookResponse = null;
        final String fields = "name,email,picture";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("https://graph.facebook.com/me")
                .queryParam("access_token", FBToken).queryParam("fields", fields);

            facebookResponse = restTemplate.getForObject(uriBuilder.toUriString(), String.class);

        } catch (HttpClientErrorException e) {
            throw new Exception("Invalid acceEss token");
        } catch (Exception exp) {
            throw new Exception("Invalid user");
        }
        return facebookResponse;
    }

    public UserGenericDTO getUserGenericInformation(Integer id)
    {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        return toUserGenericDTO(user);
    }

    public UserMeDTO getUserMeInformation(String token) 
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        return toUserMeDTO(user);
    }

    public String follow(Integer followed_id, String token, FollowRepository followRepository)
    {
        Integer follower_id = jwtTokenUtil.getIdFromToken(token);
        
        User Follower = userRepository.findById(follower_id)
        .orElseThrow(() -> new UserNotFoundException(follower_id));

        User Followed = userRepository.findById(followed_id)
        .orElseThrow(() -> new UserNotFoundException(followed_id));

        Follow NewEntity = new Follow(Follower, Followed);
        try{
        followRepository.save(NewEntity);
        }
        catch(DataIntegrityViolationException ex)
        {
            throw new DuplicateRelationshipEntryException();
        }
        catch(Exception ex)
        {
            throw new UnknownErrorException();
        }
    
        return "ok";
    }

    public String unfollow(Integer followed_id, String token, FollowRepository followRepository)
    {
        Integer follower_id = jwtTokenUtil.getIdFromToken(token);

        User Follower = userRepository.findById(follower_id)
        .orElseThrow(() -> new UserNotFoundException(follower_id));

        User Followed = userRepository.findById(followed_id)
        .orElseThrow(() -> new UserNotFoundException(followed_id));

        Follow ToDeleteEntity = followRepository.findByFromAndTo(Follower, Followed);
        if(ToDeleteEntity == null)
        throw new RelationshipNotFoundException();
        //.orElseThrow(() -> new RelationshipNotFoundException());
        followRepository.delete(ToDeleteEntity);
        return "ok";
    }

    public List<FollowerDTO> getFollowers(String token)
    {
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        User user = userRepository.findById(user_id)
        .orElseThrow(() -> new UserNotFoundException(user_id));

        Set<Follow> set = user.getFollowers();
        return toFollowerDTO(set);
    }

    public List<FollowingDTO> getFollowing(String token)
    {
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        User user = userRepository.findById(user_id)
        .orElseThrow(() -> new UserNotFoundException(user_id));

        Set<Follow> set = user.getFollowing();
        return toFollowingDTO(set);
    }

    public List<UserGenericDTO> getRandomUsers(String token)
    {
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        User owner = userRepository.findById(user_id)
        .orElseThrow(() -> new UserNotFoundException(user_id));

        Long q = userRepository.count();
        int idx = (int)(Math.random()*q);
        Page<User> randomUsers = userRepository.findAll(PageRequest.of(idx, 3));
        List<User> users = randomUsers.getContent();
        List<UserGenericDTO> FinalResult = new ArrayList<UserGenericDTO>();
        for(User pagedUser: users)
        {
            if(!userFollows(owner, pagedUser) && owner.getId() != pagedUser.getId())
                FinalResult.add(toUserGenericDTO(pagedUser, owner));
        }
        return FinalResult;
    }

    //**********************/Converters

    protected List<FollowingDTO> toFollowingDTO(Set<Follow> entities)
    {
        List<FollowingDTO> FollowingUsers = new ArrayList<FollowingDTO>();

        for(Follow entity : entities)
        {
            FollowingDTO DTO = new FollowingDTO();
            DTO.id = entity.getTo().getId();
            DTO.username = entity.getTo().getUsername();
            DTO.avatar = entity.getTo().getAvatar();
            DTO.created_at = entity.getTo().getCreated_at();

            FollowingUsers.add(DTO);
        }
        return FollowingUsers;
    }

    protected List<FollowerDTO> toFollowerDTO(Set<Follow> entities)
    {
        List<FollowerDTO> Followers = new ArrayList<FollowerDTO>();

        for(Follow entity : entities)
        {
            FollowerDTO DTO = new FollowerDTO();
            DTO.id = entity.getTo().getId();
            DTO.username = entity.getTo().getUsername();
            DTO.avatar = entity.getTo().getAvatar();
            DTO.created_at = entity.getTo().getCreated_at();

            Followers.add(DTO);
        }
        return Followers;
    }

    protected UserGenericDTO toUserGenericDTO(User entity)
    {
        UserGenericDTO DTO = new UserGenericDTO();
        DTO.id = entity.getId();
        DTO.avatar = entity.getAvatar();
        DTO.created_at = entity.getCreated_at();
        DTO.username = entity.getUsername();
        return DTO;
    }

    protected UserGenericDTO toUserGenericDTO(User entity, User owner)
    {
        UserGenericDTO DTO = new UserGenericDTO();
        DTO.id = entity.getId();
        DTO.avatar = entity.getAvatar();
        DTO.created_at = entity.getCreated_at();
        DTO.username = entity.getUsername();
        DTO.isFollowing = userFollows(owner, entity);
        return DTO;
    }

    protected boolean userFollows(User owner, User returnedUser)
    {
        Optional<Follow> optional = owner.getFollowing().stream().filter(follow -> follow.getTo().getId() == returnedUser.getId()).findFirst();
        return !optional.isEmpty();
    }

    protected UserMeDTO toUserMeDTO(User entity)
    {
        UserMeDTO DTO = new UserMeDTO();
        DTO.id = entity.getId();
        DTO.avatar = entity.getAvatar();
        DTO.created_at = entity.getCreated_at();
        DTO.username = entity.getUsername();
        DTO.email = entity.getEmail();
        return DTO;
    }

    protected User ToUserEntity(FacebookProfile facebookProfile)
    {
        User newUser = new User();
        newUser.setEmail(facebookProfile.email);
        newUser.setUsername(facebookProfile.name);
        newUser.setAvatar(facebookProfile.picture.toString());
        newUser.setFacebook_id(facebookProfile.id);

        return newUser;
    }
}