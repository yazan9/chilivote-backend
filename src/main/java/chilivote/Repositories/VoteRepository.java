package chilivote.Repositories;

import org.springframework.data.repository.CrudRepository;

import chilivote.Entities.Vote;;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface VoteRepository extends CrudRepository<Vote, Integer> {
}