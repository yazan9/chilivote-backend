package chilivote.LogicHandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
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
import chilivote.Models.Constants.ROLES;
import chilivote.Models.DTOs.UserGenericDTO;
import chilivote.Models.DTOs.UserMeDTO;
import chilivote.Repositories.FollowRepository;
import chilivote.Repositories.RolesRepository;
import chilivote.Repositories.UserRepository;

@Component
public class UserLogicHandler
{
    @Autowired
    private RoleLogicHandler roleLogicHandler;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired 
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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
        User userEntity = ToUserEntity(facebookProfile);
        userEntity.setRole(rolesRepository.findByName(ROLES.VIEWER));
        User SavedUser = userRepository.save(userEntity);
        
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

    public String getRole(String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        return user.getRole().getName();
    }

    public String follow(Integer followed_id, String token, FollowRepository followRepository)
    {
        Integer follower_id = jwtTokenUtil.getIdFromToken(token);
        
        User Follower = userRepository.findById(follower_id)
        .orElseThrow(() -> new UserNotFoundException(follower_id));

        User Followed = userRepository.findById(followed_id)
        .orElseThrow(() -> new UserNotFoundException(followed_id));

        Follow NewEntity = new Follow(Follower, Followed);
        String newFollowerRole = null;
        try{
            followRepository.save(NewEntity);
            newFollowerRole = this.roleLogicHandler.updateRole(Follower);
            this.roleLogicHandler.updateRole(Followed);
        }
        catch(DataIntegrityViolationException ex)
        {
            throw new DuplicateRelationshipEntryException();
        }
        catch(Exception ex)
        {
            throw new UnknownErrorException();
        }
    
        return newFollowerRole;
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
        String newFollowerRole = this.roleLogicHandler.updateRole(Follower);
        this.roleLogicHandler.updateRole(Followed);
        return newFollowerRole;
    }

    public List<UserGenericDTO> search(String token, String query)
    {
        Integer user_id = jwtTokenUtil.getIdFromToken(token);

        userRepository.findById(user_id)
        .orElseThrow(() -> new UserNotFoundException(user_id));

        List<User> results = userRepository.search(query);
        
        return toUserGenericDTOList(results);
    }

    public List<UserGenericDTO> getFollowers(String token)
    {
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        User user = userRepository.findById(user_id)
        .orElseThrow(() -> new UserNotFoundException(user_id));

        Set<Follow> set = user.getFollowers();
        
        return toUserGenericDTOList(set, user, Relationship.from);
    }

    public List<UserGenericDTO> getFollowing(String token)
    {
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        User user = userRepository.findById(user_id)
        .orElseThrow(() -> new UserNotFoundException(user_id));

        Set<Follow> set = user.getFollowing();
        return toUserGenericDTOList(set, user, Relationship.to);
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
                FinalResult.add(toUserGenericDTO(pagedUser, owner, false));
        }
        return FinalResult;
    }

    //**********************/Converters /*************************//

    protected UserGenericDTO toUserGenericDTO(User entity)
    {
        UserGenericDTO DTO = new UserGenericDTO();
        DTO.id = entity.getId();
        DTO.avatar = entity.getAvatar();
        DTO.created_at = entity.getCreated_at();
        DTO.username = entity.getUsername();
        return DTO;
    }

    protected UserGenericDTO toUserGenericDTO(User entity, User owner, boolean setFollowingToTrue)
    {
        UserGenericDTO DTO = toUserGenericDTO(entity);
        DTO.isFollowing = setFollowingToTrue ? true : userFollows(owner, entity);
        return DTO;
    }

    protected List<UserGenericDTO> toUserGenericDTOList(List<User> entities)
    {
        List<UserGenericDTO> finalResult = new ArrayList<UserGenericDTO>();
        for(User entity:entities){
            finalResult.add(toUserGenericDTO(entity));
        }

        return finalResult;
    }

    protected List<UserGenericDTO> toUserGenericDTOList(Set<Follow> set, User owner, Relationship relationship){
        List<UserGenericDTO> FinalResult = new ArrayList<UserGenericDTO>();
        for(Follow follow:set){
            User target = relationship == Relationship.to ? follow.getTo() : follow.getFrom();
            FinalResult.add(toUserGenericDTO(target, owner, relationship == Relationship.to));
        }
        return FinalResult;
    }

    protected enum Relationship{
        to,
        from
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
        DTO.role = entity.getRole().getName();
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