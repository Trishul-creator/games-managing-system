package com.example.quizbowl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class GameService {

    private final GameStateRepository stateRepo;
    private final GameEventRepository eventRepo;

    public GameService(GameStateRepository stateRepo, GameEventRepository eventRepo) {
        this.stateRepo = stateRepo;
        this.eventRepo = eventRepo;
    }

    private GameStateEntity stateFor(String gameId) {
        String id = StringUtils.hasText(gameId) ? gameId.trim() : "default";
        return stateRepo.findById(id).orElseGet(() -> {
            GameStateEntity e = new GameStateEntity();
            e.setGameId(id);
            e.setTeamAName("Team A");
            e.setTeamBName("Team B");
            e.setQuestionNumber(1);
            e.setTeamAScore(0);
            e.setTeamBScore(0);
            e.setLastTossupWinner(null);
            return stateRepo.save(e);
        });
    }

    public GameState getState(String gameId) {
        GameStateEntity e = stateFor(gameId);
        GameState dto = new GameState();
        dto.setTeamAName(e.getTeamAName());
        dto.setTeamBName(e.getTeamBName());
        dto.setTeamAScore(e.getTeamAScore());
        dto.setTeamBScore(e.getTeamBScore());
        dto.setQuestionNumber(e.getQuestionNumber());
        dto.setLastTossupWinner(e.getLastTossupWinner());
        List<GameEvent> history = eventRepo.findByGameIdOrderByTimestampDesc(e.getGameId())
                .stream()
                .map(this::toDto)
                .toList();
        dto.setHistory(history);
        return dto;
    }

    private GameEvent toDto(GameEventEntity entity) {
        GameEvent ev = new GameEvent();
        ev.setType(entity.getType());
        ev.setDescription(entity.getDescription());
        ev.setTimestamp(entity.getTimestamp());
        ev.setTeam(entity.getTeam());
        ev.setPoints(entity.getPoints());
        return ev;
    }

    private void addEvent(GameStateEntity state, String type, String desc, String team, Integer points) {
        GameEventEntity ev = new GameEventEntity();
        ev.setGameId(state.getGameId());
        ev.setType(type);
        ev.setDescription(desc);
        ev.setTimestamp(System.currentTimeMillis());
        ev.setTeam(team);
        ev.setPoints(points);
        eventRepo.save(ev);
    }

    public void setTeamNames(String gameId, String teamAName, String teamBName) {
        GameStateEntity state = stateFor(gameId);
        if (StringUtils.hasText(teamAName)) {
            state.setTeamAName(teamAName.trim());
        }
        if (StringUtils.hasText(teamBName)) {
            state.setTeamBName(teamBName.trim());
        }
        stateRepo.save(state);
    }

    public void resetGame(String gameId) {
        GameStateEntity state = stateFor(gameId);
        state.setTeamAScore(0);
        state.setTeamBScore(0);
        state.setQuestionNumber(1);
        state.setLastTossupWinner(null);
        stateRepo.save(state);
        eventRepo.deleteAll(eventRepo.findByGameIdOrderByTimestampDesc(state.getGameId()));
    }

    public void nextTossup(String gameId) {
        GameStateEntity state = stateFor(gameId);
        state.setQuestionNumber(state.getQuestionNumber() + 1);
        state.setLastTossupWinner(null);
        stateRepo.save(state);
        addEvent(state, "NEXT_TOSSUP", "Moving to tossup #" + state.getQuestionNumber(), null, null);
    }

    public void awardTossup(String gameId, String team) {
        GameStateEntity state = stateFor(gameId);
        if (!"A".equals(team) && !"B".equals(team)) {
            return;
        }
        if ("A".equals(team)) {
            state.setTeamAScore(state.getTeamAScore() + 10);
            state.setLastTossupWinner("A");
            addEvent(state, "TOSSUP", "Tossup +10 → " + state.getTeamAName(), "A", 10);
        } else {
            state.setTeamBScore(state.getTeamBScore() + 10);
            state.setLastTossupWinner("B");
            addEvent(state, "TOSSUP", "Tossup +10 → " + state.getTeamBName(), "B", 10);
        }
        stateRepo.save(state);
    }

    public void awardBonus(String gameId, int points) {
        GameStateEntity state = stateFor(gameId);
        if (state.getLastTossupWinner() == null) {
            return;
        }
        if ("A".equals(state.getLastTossupWinner())) {
            state.setTeamAScore(state.getTeamAScore() + points);
            addEvent(state, "BONUS", "Bonus +" + points + " → " + state.getTeamAName(), "A", points);
        } else if ("B".equals(state.getLastTossupWinner())) {
            state.setTeamBScore(state.getTeamBScore() + points);
            addEvent(state, "BONUS", "Bonus +" + points + " → " + state.getTeamBName(), "B", points);
        }
        stateRepo.save(state);
    }
}
