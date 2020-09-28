package chilivote.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Table(name="follows")
@Entity
public class FollowEntity implements Serializable
{
    private static final long serialVersionUID = 8495817802073010928L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="from_user_fk")
    private UserEntity from;

    @ManyToOne
    @JoinColumn(name="to_user_fk")
    private UserEntity to;

    public FollowEntity(UserEntity from, UserEntity to) {
        this.from = from;
        this.to = to;
    }
}