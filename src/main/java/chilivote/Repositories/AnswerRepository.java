package chilivote.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import chilivote.entities.AnswerEntity;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Integer> {
}