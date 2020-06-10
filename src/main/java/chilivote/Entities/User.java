package chilivote.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import chilivote.Models.UserPreference;

@Entity
public class User implements Serializable
{
    private static final long serialVersionUID = -748956247024967638L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    @Column(unique=true)
    private String email;

    private String avatar;

    private String facebook_id;

    private String preferences;

    private String password;

    @CreationTimestamp
    private LocalDateTime created_at;

    @UpdateTimestamp
    private LocalDateTime updated_at;

    @ManyToOne
    private Role role;

    //Getters and Setters

    public List<PermissionRole> getPermissions(){
        return this.role.getPermissions();
    }

    public Role getRole(){
        return role;
    }

    public void setRole(Role role){
        this.role = role;
    }

    public Integer getId()
    {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    public Set<Chilivote> getChilivotes() {
        return chilivotes;
    }

    public void setChilivotes(Set<Chilivote> chilivotes) {
        this.chilivotes = chilivotes;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }

    public Set<Follow> getFollowers() {
        return followers;
    }

    public Set<Follow> getFollowing() {
        return following;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //Navigation Properties
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Chilivote> chilivotes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Answer> answers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Vote> votes;

    @OneToMany(mappedBy="to", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Follow> followers;

    @OneToMany(mappedBy="from", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Follow> following;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Notification> notifications;

    public UserPreference getPreferences() {
        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.readValue(this.preferences, UserPreference.class);
        }
        catch(Exception e){
            return new UserPreference();
        }
    }

    public void setPreferences(UserPreference preferences) {
        ObjectMapper mapper = new ObjectMapper();
        try{
            this.preferences = mapper.writeValueAsString(preferences);
        }
        catch(Exception e){
            this.preferences = "";
        }
    }

    public Set<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        this.notifications = notifications;
    }
}