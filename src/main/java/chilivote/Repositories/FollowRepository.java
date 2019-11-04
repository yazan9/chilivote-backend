package chilivote.Repositories;

import org.springframework.data.repository.CrudRepository;

import chilivote.Entities.Follow;
import chilivote.Entities.User;
import chilivote.Models.FollowId;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface FollowRepository extends CrudRepository<Follow, FollowId> {
    Follow findByFromAndTo(User FromUserFk, User ToUserFk);
}