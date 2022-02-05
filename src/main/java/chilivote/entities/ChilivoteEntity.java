package chilivote.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "chilivotes")
@Entity
public class ChilivoteEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean isPrivate;

    private String followers;

    @Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		ChilivoteEntity chilivote = (ChilivoteEntity) o;
		return Objects.equals( id, chilivote.id );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id );
	}

    @OneToMany(mappedBy = "chilivote", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<AnswerEntity> answers;

    @OneToMany(mappedBy = "chilivote", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<VoteEntity> votes;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @OneToMany(mappedBy = "chilivote", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<NotificationEntity> notifications;
}