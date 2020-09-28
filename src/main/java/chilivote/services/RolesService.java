package chilivote.services;

import java.util.List;
import java.util.Set;

import chilivote.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import chilivote.entities.ChilivoteEntity;
import chilivote.entities.RoleEntity;
import chilivote.entities.UserEntity;
import chilivote.entities.VoteEntity;
import chilivote.models.constants.ROLES;
import chilivote.Repositories.RolesRepository;
import chilivote.Repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RolesService {
    private int votedOnPosts;
    private int iFollow;
    private int peopleWhoFollowMe;
    private int votesOnMyPosts;
    private UserEntity user;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    UserRepository usersRepository;

    public String updateRole(UserEntity user) {
        this.user = user;
        this.votedOnPosts = user.getVotes().size();
        this.iFollow = user.getFollowing().size();
        this.peopleWhoFollowMe = user.getFollowers().size();
        this.votesOnMyPosts = 0;
        for (ChilivoteEntity chilivote : user.getChilivotes()) {
            List<VoteEntity> _votesOnMyPosts = chilivote.getVotes();
            if(_votesOnMyPosts != null)
                votesOnMyPosts += _votesOnMyPosts.size();
        }

        if(checkIfSuper())
            return ROLES.SUPER;
        else if (checkIfChilivoter())
            return setRole(ROLES.CHILIVOTER);
        else if (checkIfLegend())
            return setRole(ROLES.LEGEND);
        else if (checkIfMaster())
            return setRole(ROLES.MASTER);
        else if (checkIfDecent())
            return setRole(ROLES.DECENT);
        else if (checkIfActive())
            return setRole(ROLES.ACTIVE);
        else if (checkIfVoter())
            return setRole(ROLES.VOTER);
        else
            return setRole(ROLES.VIEWER);
    }

    protected String setRole(String roleName) {
        RoleEntity role = rolesRepository.findByName(roleName).orElseThrow(() -> new EntityNotFoundException(roleName, "Role"));
        this.user.setRole(role);
        this.usersRepository.save(this.user);
        return roleName;
    }

    protected boolean checkIfSuper(){
        return this.user.getRole().getName().equals(ROLES.SUPER);
    }

    protected boolean checkIfChilivoter() {
        //return this.peopleWhoFollowMe >= 500000;
        return 
        this.peopleWhoFollowMe >= 6 && 
        this.votedOnPosts >= 6 && 
        this.iFollow >= 6 &&
        this.votesOnMyPosts >= 6;
    }

    protected boolean checkIfLegend() {
        //return this.peopleWhoFollowMe >= 200000 && this.votedOnPosts >= 1000 && this.iFollow >= 150;
        return 
        this.peopleWhoFollowMe >= 5 && 
        this.votedOnPosts >= 5 && 
        this.iFollow >= 5 &&
        this.votesOnMyPosts >= 5;
    }

    protected boolean checkIfMaster() {
        //return this.peopleWhoFollowMe >= 100 && this.votesOnMyPosts >= 200;
        return 
        this.peopleWhoFollowMe >= 4 && 
        this.votedOnPosts >= 4 && 
        this.iFollow >= 4 &&
        this.votesOnMyPosts >= 4;
    }

    protected boolean checkIfDecent() {
        //return this.iFollow >= 50 && this.votedOnPosts >= 100 && this.votesOnMyPosts >= 100;
        return 
        this.peopleWhoFollowMe >= 3 && 
        this.votedOnPosts >= 3 && 
        this.iFollow >= 3 &&
        this.votesOnMyPosts >= 3;
    }

    protected boolean checkIfActive() {
        //return this.votedOnPosts >= 100 && this.votesOnMyPosts >= 30;
        return 
        this.votedOnPosts >= 2 && 
        this.iFollow >= 2 &&
        this.votesOnMyPosts >= 2;
    }

    protected boolean checkIfVoter() {
        //return this.votedOnPosts >= 20;
        return this.votedOnPosts >= 1;
    }
}