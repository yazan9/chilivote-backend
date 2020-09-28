package chilivote.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import chilivote.entities.ChilivoteEntity;

public interface ChilivoteRepository extends JpaRepository<ChilivoteEntity, Integer> {
    Optional<List<ChilivoteEntity>> findByUserId(Integer id);
    Page<ChilivoteEntity> findAll(Pageable pageable);
}