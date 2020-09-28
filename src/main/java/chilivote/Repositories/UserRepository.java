package chilivote.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import chilivote.entities.UserEntity;
import org.springframework.data.repository.query.Param;;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
    Page<UserEntity> findAll(Pageable pageable);

    @Query(value = "Select * from user where username like %:q%", nativeQuery = true)
    List<UserEntity> search(@Param("q")String query);
}