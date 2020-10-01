package chilivote.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import chilivote.entities.NotificationEntity;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer> {
  Optional<List<NotificationEntity>> findByChilivoteId(Integer chilivoteId);
}