package com.example.quizbowl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<String, GameState> games = new ConcurrentHashMap<>();

    private GameState stateFor(String gameId) {
        return games.computeIfAbsent(
                StringUtils.hasText(gameId) ? gameId.trim() : "default",
                id -> new GameState()
        );
    }

    public GameState getState(String gameId) {
        return stateFor(gameId);
    }

    public void setTeamNames(String gameId, String teamAName, String teamBName) {
        GameState state = stateFor(gameId);
        if (StringUtils.hasText(teamAName)) {
            state.setTeamAName(teamAName.trim());
        }
        if (StringUtils.hasText(teamBName)) {
            state.setTeamBName(teamBName.trim());
        }
    }

    public void resetGame(String gameId) {
        GameState state = stateFor(gameId);
        state.setTeamAScore(0);
        state.setTeamBScore(0);
        state.setQuestionNumber(1);
        state.setLastTossupWinner(null);
        state.getHistory().clear();
    }

    public void nextTossup(String gameId) {
        GameState state = stateFor(gameId);
        state.setQuestionNumber(state.getQuestionNumber() + 1);
        state.setLastTossupWinner(null);
        state.getHistory().add(new GameEvent(
                "NEXT_TOSSUP",
                "Moving to tossup #" + state.getQuestionNumber(),
                null,
                null
        ));
    }

    public void awardTossup(String gameId, String team) {
        GameState state = stateFor(gameId);
        if (!"A".equals(team) && !"B".equals(team)) {
            return;
        }
        if ("A".equals(team)) {
            state.setTeamAScore(state.getTeamAScore() + 10);
            state.setLastTossupWinner("A");
            state.getHistory().add(new GameEvent(
                    "TOSSUP",
                    "Tossup +10 → " + state.getTeamAName(),
                    "A",
                    10
            ));
        } else {
            state.setTeamBScore(state.getTeamBScore() + 10);
            state.setLastTossupWinner("B");
            state.getHistory().add(new GameEvent(
                    "TOSSUP",
                    "Tossup +10 → " + state.getTeamBName(),
                    "B",
                    10
            ));
        }
    }

    public void awardBonus(String gameId, int points) {
        GameState state = stateFor(gameId);
        if (state.getLastTossupWinner() == null) {
            return;
        }
        if ("A".equals(state.getLastTossupWinner())) {
            state.setTeamAScore(state.getTeamAScore() + points);
            state.getHistory().add(new GameEvent(
                    "BONUS",
                    "Bonus +" + points + " → " + state.getTeamAName(),
                    "A",
                    points
            ));
        } else if ("B".equals(state.getLastTossupWinner())) {
            state.setTeamBScore(state.getTeamBScore() + points);
            state.getHistory().add(new GameEvent(
                    "BONUS",
                    "Bonus +" + points + " → " + state.getTeamBName(),
                    "B",
                    points
            ));
        }
    }
}
