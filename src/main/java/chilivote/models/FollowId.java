package chilivote.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FollowId implements Serializable
{
    private static final long serialVersionUID = -2550185165626007488L;
 
    @Column(name = "follower_id")
    private Integer follower_id;
 
    @Column(name = "followed_id")
    private Integer followed_id;
 
    public FollowId() {
    }
 
    public FollowId(Integer follower_id, Integer followed_id) {
        this.follower_id = follower_id;
        this.followed_id = followed_id;
    }
 
    public Integer getFollowerId() {
        return follower_id;
    }
 
    public Integer getFollowedId() {
        return followed_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FollowId)) return false;
        FollowId that = (FollowId) o;
        return Objects.equals(getFollowerId(), that.getFollowerId()) &&
                Objects.equals(getFollowedId(), that.getFollowedId());
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(getFollowerId(), getFollowedId());
    }
}