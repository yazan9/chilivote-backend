package chilivote.LogicHandlers;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import chilivote.Entities.User;
import chilivote.JWT.JwtTokenUtil;
import chilivote.Models.FacebookProfile;
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
        final String token = jwtTokenUtil.generateToken(userDetails.getEmail());
        return token;
    }

    protected String CreateNewUser(FacebookProfile facebookProfile)
    {
        User SavedUser = userRepository.save(ToUserEntity(facebookProfile));
        return jwtTokenUtil.generateToken(SavedUser.getEmail());
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
}