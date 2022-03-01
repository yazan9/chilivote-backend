package chilivote.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import chilivote.exceptions.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import chilivote.entities.FollowEntity;
import chilivote.entities.UserEntity;
import chilivote.jwt.JwtTokenUtil;
import chilivote.models.FacebookProfile;
import chilivote.models.UserPreference;
import chilivote.models.constants.ROLES;
import chilivote.models.domain.UserGenericDTO;
import chilivote.models.domain.UserMeDTO;
import chilivote.Repositories.FollowRepository;
import chilivote.Repositories.RolesRepository;
import chilivote.Repositories.UserRepository;

@Service
public class UsersService
{
    @Autowired
    private RolesService roleLogicHandler;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired 
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public String getJWTToken(String FBToken) throws Exception
    {
        FacebookProfile facebookProfile = new ObjectMapper().readValue(getFBProfileFromToken(FBToken), FacebookProfile.class);     
        UserEntity userDetails = userRepository.findByEmail(facebookProfile.email).orElse(null);
        if(userDetails == null)
            return CreateNewUser(facebookProfile);
        final String token = jwtTokenUtil.generateToken(userDetails);
        updateFacebookAvatar(facebookProfile, userDetails);
        return token;
    }

    public void updateFacebookAvatar(FacebookProfile facebookProfile, UserEntity userEntity){
        userEntity.setAvatar(facebookProfile.picture.toString());
        userRepository.save(userEntity);
    }

    protected String CreateNewUser(FacebookProfile facebookProfile)
    {
        UserEntity userEntity = ToUserEntity(facebookProfile);
        userEntity.setRole(rolesRepository.findByName(ROLES.VIEWER).orElseThrow(() -> new EntityNotFoundException(ROLES.VIEWER, "Role")));
        UserEntity SavedUser = userRepository.save(userEntity);
        
        return jwtTokenUtil.generateToken(SavedUser);
    }

    protected String getFBProfileFromToken(String FBToken) throws Exception
    {
        RestTemplate restTemplate = new RestTemplate();
        String facebookResponse = null;
        final String fields = "name,email,picture.type(small)";

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
        UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        return toUserGenericDTO(user);
    }

    public UserMeDTO getUserMeInformation(String token) 
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        return toUserMeDTO(user);
    }

    public String getRole(String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        return user.getRole().getName();
    }

    public String follow(Integer followed_id, String token, FollowRepository followRepository)
    {
        Integer follower_id = jwtTokenUtil.getIdFromToken(token);
        
        UserEntity Follower = userRepository.findById(follower_id)
        .orElseThrow(() -> new UserNotFoundException(follower_id));

        UserEntity Followed = userRepository.findById(followed_id)
        .orElseThrow(() -> new UserNotFoundException(followed_id));

        FollowEntity NewEntity = new FollowEntity(Follower, Followed);
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

        UserEntity Follower = userRepository.findById(follower_id)
        .orElseThrow(() -> new UserNotFoundException(follower_id));

        UserEntity Followed = userRepository.findById(followed_id)
        .orElseThrow(() -> new UserNotFoundException(followed_id));

        FollowEntity ToDeleteEntity = followRepository.findByFromAndTo(Follower, Followed).orElse(null);
        if(ToDeleteEntity == null)
        throw new RelationshipNotFoundException();
        followRepository.delete(ToDeleteEntity);
        String newFollowerRole = this.roleLogicHandler.updateRole(Follower);
        this.roleLogicHandler.updateRole(Followed);
        return newFollowerRole;
    }

    public List<UserGenericDTO> search(String token, String query)
    {
        Integer user_id = jwtTokenUtil.getIdFromToken(token);

        UserEntity owner = userRepository.findById(user_id)
        .orElseThrow(() -> new UserNotFoundException(user_id));

        List<UserEntity> results = userRepository.search(query);

        List<UserGenericDTO> FinalResult = new ArrayList<UserGenericDTO>();
        for(UserEntity u: results)
        {
            if(!userFollows(owner, u) && owner.getId() != u.getId())
                FinalResult.add(toUserGenericDTO(u, owner, false));
        }
        return FinalResult;
    }

    public List<UserGenericDTO> getFollowers(String token)
    {
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        UserEntity user = userRepository.findById(user_id)
        .orElseThrow(() -> new UserNotFoundException(user_id));

        List<FollowEntity> set = user.getFollowers();
        
        return toUserGenericDTOList(set, user, Relationship.from);
    }

    public List<UserGenericDTO> getFollowing(String token)
    {
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        UserEntity user = userRepository.findById(user_id)
        .orElseThrow(() -> new UserNotFoundException(user_id));

        List<FollowEntity> set = user.getFollowing();
        return toUserGenericDTOList(set, user, Relationship.to);
    }

    public List<UserGenericDTO> getRandomUsers(String token)
    {
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        UserEntity owner = userRepository.findById(user_id)
        .orElseThrow(() -> new UserNotFoundException(user_id));

        Long q = userRepository.count();
        int idx = (int)(Math.random()*q);
        Page<UserEntity> randomUsers = userRepository.findAll(PageRequest.of(idx, 3));
        List<UserEntity> users = randomUsers.getContent();
        List<UserGenericDTO> FinalResult = new ArrayList<UserGenericDTO>();
        for(UserEntity pagedUser: users)
        {
            if(!userFollows(owner, pagedUser) && owner.getId() != pagedUser.getId())
                FinalResult.add(toUserGenericDTO(pagedUser, owner, false));
        }
        return FinalResult;
    }

    public ResponseEntity<?> hideUser(String token, Integer id){
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        UserEntity owner = userRepository.findById(user_id)
        .orElseThrow(() -> new UserNotFoundException(user_id));

        UserPreference preferences = owner.getPreferences();
        preferences.hide.add(id);
        
        return ResponseEntity.ok().build();
    }

    //**********************/Converters /*************************//

    protected UserGenericDTO toUserGenericDTO(UserEntity entity)
    {
        UserGenericDTO DTO = new UserGenericDTO();
        DTO.id = entity.getId();
        DTO.avatar = entity.getAvatar();
        DTO.created_at = entity.getCreatedAt();
        DTO.username = entity.getUsername();
        return DTO;
    }

    protected UserGenericDTO toUserGenericDTO(UserEntity entity, UserEntity owner, boolean setFollowingToTrue)
    {
        UserGenericDTO DTO = toUserGenericDTO(entity);
        DTO.isFollowing = setFollowingToTrue ? true : userFollows(owner, entity);
        return DTO;
    }

    protected List<UserGenericDTO> toUserGenericDTOList(List<UserEntity> entities)
    {
        List<UserGenericDTO> finalResult = new ArrayList<UserGenericDTO>();
        for(UserEntity entity:entities){
            finalResult.add(toUserGenericDTO(entity));
        }

        return finalResult;
    }

    protected List<UserGenericDTO> toUserGenericDTOList(List<FollowEntity> set, UserEntity owner, Relationship relationship){
        List<UserGenericDTO> FinalResult = new ArrayList<UserGenericDTO>();
        for(FollowEntity follow:set){
            UserEntity target = relationship == Relationship.to ? follow.getTo() : follow.getFrom();
            FinalResult.add(toUserGenericDTO(target, owner, relationship == Relationship.to));
        }
        return FinalResult;
    }

    protected enum Relationship{
        to,
        from
    }

    protected boolean userFollows(UserEntity owner, UserEntity returnedUser)
    {
        Optional<FollowEntity> optional = owner.getFollowing().stream().filter(follow -> follow.getTo().getId() == returnedUser.getId()).findFirst();
        return !optional.isEmpty();
    }

    protected UserMeDTO toUserMeDTO(UserEntity entity)
    {
        UserMeDTO DTO = new UserMeDTO();
        DTO.id = entity.getId();
        DTO.avatar = entity.getAvatar();
        DTO.created_at = entity.getCreatedAt();
        DTO.username = entity.getUsername();
        DTO.email = entity.getEmail();
        DTO.role = entity.getRole().getName();
        return DTO;
    }

    protected UserEntity ToUserEntity(FacebookProfile facebookProfile)
    {
        UserEntity newUser = new UserEntity();
        newUser.setEmail(facebookProfile.email);
        newUser.setUsername(facebookProfile.name);
        newUser.setAvatar(facebookProfile.picture.toString());
        newUser.setFacebook_id(facebookProfile.id);

        return newUser;
    }
}