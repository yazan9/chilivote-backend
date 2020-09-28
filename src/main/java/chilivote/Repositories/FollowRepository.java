package chilivote.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import chilivote.entities.FollowEntity;
import chilivote.entities.UserEntity;
import chilivote.models.FollowId;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<FollowEntity, FollowId> {
    Optional<FollowEntity> findByFromAndTo(UserEntity FromUserFk, UserEntity ToUserFk);
}