package chilivote.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import chilivote.entities.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer> {}