package com.example.quizbowl;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GameStateRepository extends JpaRepository<GameStateEntity, String> {
}
