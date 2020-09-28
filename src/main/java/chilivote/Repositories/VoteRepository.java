package chilivote.Repositories;

import chilivote.entities.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;;

public interface VoteRepository extends JpaRepository<VoteEntity, Integer> {}