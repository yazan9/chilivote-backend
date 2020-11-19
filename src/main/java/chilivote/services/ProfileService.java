package chilivote.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import chilivote.entities.ChilivoteEntity;
import chilivote.entities.UserEntity;
import chilivote.entities.VoteEntity;
import chilivote.models.domain.ProfileDTO;
import chilivote.Repositories.UserRepository;

@Service
public class ProfileService {

    @Autowired
    private CommonService commonService;

    @Autowired
    private UserRepository userRepository;

    public ProfileDTO getProfile(String token){
        UserEntity owner = this.commonService.getOwner(token);
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.avatar = owner.getAvatar();
        profileDTO.created_at = owner.getCreatedAt();
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
        UserEntity owner = this.commonService.getOwner(token);
        owner.setAvatar("{url=".concat(profile.avatar).concat("}"));
        owner.setUsername(profile.username);
        this.userRepository.save(owner);
        return ResponseEntity.ok().build();
    }

    protected int getVotesOnMyPosts(UserEntity user){
        int votesOnMyPosts = 0;
        for (ChilivoteEntity chilivote : user.getChilivotes()) {
            List<VoteEntity> _votesOnMyPosts = chilivote.getVotes();
            if(_votesOnMyPosts != null)
                votesOnMyPosts += _votesOnMyPosts.size();
        }
        return votesOnMyPosts;
    }
}