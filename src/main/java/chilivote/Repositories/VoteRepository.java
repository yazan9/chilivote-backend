package chilivote.Repositories;

import chilivote.entities.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;;import java.util.Optional;

public interface VoteRepository extends JpaRepository<VoteEntity, Integer> {
    Optional<VoteEntity> findByUserIdAndAnswerId(Integer userId, Integer answerId);
    Optional<VoteEntity> findByUserIdAndChilivoteId(Integer userId, Integer chilivoteId);
    void deleteByUserIdAndAnswerId(Integer userId, Integer answerId);
    void deleteByUserIdAndChilivoteId(Integer userId, Integer answerId);
}