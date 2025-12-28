package com.example.quizbowl;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameEventRepository extends JpaRepository<GameEventEntity, Long> {
    List<GameEventEntity> findByGameIdOrderByTimestampDesc(String gameId);
}
