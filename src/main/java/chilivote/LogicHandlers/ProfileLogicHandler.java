package chilivote.LogicHandlers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import chilivote.Entities.Chilivote;
import chilivote.Entities.User;
import chilivote.Entities.Vote;
import chilivote.Models.DTOs.ProfileDTO;
import chilivote.Repositories.UserRepository;
import chilivote.Services.CommonService;

@Service
public class ProfileLogicHandler {

    @Autowired
    private CommonService commonService;

    @Autowired
    private UserRepository userRepository;

    public ProfileDTO getProfile(String token){
        User owner = this.commonService.getOwner(token);
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.avatar = owner.getAvatar();
        profileDTO.created_at = owner.getCreated_at();
        profileDTO.email = owner.getEmail();
        profileDTO.id = owner.getId();
        profileDTO.role = owner.getRole().getName();
        profileDTO.username = owner.getUsername();
        profileDTO.followers = owner.getFollowers().size();
        profileDTO.following = owner.getFollowing().size();
        profileDTO.votedOn = owner.getVotes().size();
        profileDTO.receivedVotesOn = this.getVotesOnMyPosts(owner);
        profileDTO.posts = owner.getChilivotes().size();

        return profileDTO;
    }

    public ResponseEntity<?> updateProfile(String token, ProfileDTO profile){
        User owner = this.commonService.getOwner(token);
        owner.setAvatar("{url='".concat(profile.avatar).concat("'}"));
        owner.setUsername(profile.username);
        this.userRepository.save(owner);
        return ResponseEntity.ok().build();
    }

    protected int getVotesOnMyPosts(User user){
        int votesOnMyPosts = 0;
        for (Chilivote chilivote : user.getChilivotes()) {
            Set<Vote> _votesOnMyPosts = chilivote.getVotes();
            if(_votesOnMyPosts != null)
                votesOnMyPosts += _votesOnMyPosts.size();
        }
        return votesOnMyPosts;
    }
}