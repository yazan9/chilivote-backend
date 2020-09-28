package chilivote.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import chilivote.entities.RoleEntity;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);
}