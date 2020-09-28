package chilivote.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import chilivote.models.UserPreference;

@Setter
@Getter
@NoArgsConstructor
@Table(name="users")
@Entity
public class UserEntity implements Serializable
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
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    private RoleEntity role;

    //Navigation Properties
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ChilivoteEntity> chilivotes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnswerEntity> answers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<VoteEntity> votes;

    @OneToMany(mappedBy="to", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FollowEntity> followers;

    @OneToMany(mappedBy="from", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FollowEntity> following;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<NotificationEntity> notifications;

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
}