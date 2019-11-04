package chilivote.Repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import chilivote.Entities.Chilivote;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ChilivoteRepository extends CrudRepository<Chilivote, Integer> {
    List<Chilivote> findByUserId(Integer id);
}