package chilivote.LogicHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import chilivote.Entities.Chilivote;
import chilivote.Entities.Role;
import chilivote.Entities.User;
import chilivote.Models.Constants.ROLES;
import chilivote.Repositories.RolesRepository;
import chilivote.Repositories.UserRepository;

@Component
public class RoleLogicHandler{
    private int votedOnPosts;
    private int iFollow;
    private int peopleWhoFollowMe;
    private int votesOnMyPosts;
    private User user;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired 
    UserRepository usersRepository;

    public void updateRole(User user){
        this.user = user;
        this.votedOnPosts = user.getVotes().size();
        this.iFollow = user.getFollowing().size();
        this.peopleWhoFollowMe = user.getFollowers().size();
        this.votesOnMyPosts = 0;
        for (Chilivote chilivote : user.getChilivotes()) {
            votesOnMyPosts += chilivote.getVotes().size();                    
        }

        if(checkIfChilivoter())
            setRole(ROLES.CHILIVOTER);
        else if(checkIfLegend())
            setRole(ROLES.LEGEND);
        else if(checkIfMaster())
            setRole(ROLES.MASTER);
        else if(checkIfDecent())
            setRole(ROLES.DECENT);
        else if(checkIfActive())
            setRole(ROLES.ACTIVE);
        else if(checkIfVoter())
            setRole(ROLES.VOTER);
        else
            setRole(ROLES.VIEWER);
    }

    protected void setRole(String roleName){
        Role role = rolesRepository.findByName(roleName);
        this.user.setRole(role);
        this.usersRepository.save(this.user);
    }

    protected boolean checkIfChilivoter(){
        return this.peopleWhoFollowMe >= 500000;
    }

    protected boolean checkIfLegend(){
        return this.peopleWhoFollowMe >= 200000 && this.votedOnPosts >= 1000 && this.iFollow >= 150;
    }

    protected boolean checkIfMaster(){
        return this.peopleWhoFollowMe >= 100 && this.votesOnMyPosts >=200;
    }

    protected boolean checkIfDecent(){
        return this.iFollow >= 50 && this.votedOnPosts >= 100 && this.votesOnMyPosts >= 100;
    }

    protected boolean checkIfActive(){
        return this.votedOnPosts >= 100 && this.votesOnMyPosts >= 30;
    }

    protected boolean checkIfVoter(){
        return this.votedOnPosts >= 20;
    }
}