package com.example.quizbowl;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BracketMatchRepository extends JpaRepository<BracketMatchEntity, String> {
    List<BracketMatchEntity> findByBracket(String bracket);
    List<BracketMatchEntity> findByCompletedFalse();
}
