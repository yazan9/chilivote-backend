package chilivote.Repositories;

import org.springframework.data.repository.CrudRepository;

import chilivote.Entities.Role;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface RolesRepository extends CrudRepository<Role, Long> {
    public Role findByName(String name);
}