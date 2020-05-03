package chilivote.Repositories;

import org.springframework.data.repository.CrudRepository;

import chilivote.Entities.Notification;

public interface NotificationRepository extends CrudRepository<Notification, Integer> {

}