package com.example.quizbowl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

@Service
public class BracketService {
    private final BracketState state = new BracketState();
    private final GameService gameService;
    private final Map<String, String> currentTeamAByGame = new HashMap<>();
    private final Map<String, String> currentTeamBByGame = new HashMap<>();

    public BracketService(GameService gameService) {
        this.gameService = gameService;
    }

    public synchronized BracketState getState() {
        updateFinishedFlag();
        computeSuggestions();
        return state;
    }

    public synchronized void initBracket(List<String> teamNames) {
        state.getTeams().clear();
        state.getMatches().clear();
        state.setCurrentTeamAId(null);
        state.setCurrentTeamBId(null);
        state.setFinished(false);
        state.setSuggestedWinnersTeamAId(null);
        state.setSuggestedWinnersTeamBId(null);
        state.setSuggestedLosersTeamAId(null);
        state.setSuggestedLosersTeamBId(null);
        state.getSuggestedWinnersPairs().clear();
        state.getSuggestedLosersPairs().clear();
        currentTeamAByGame.clear();
        currentTeamBByGame.clear();

        if (teamNames == null) {
            return;
        }

        for (String raw : teamNames) {
            if (StringUtils.hasText(raw)) {
                state.getTeams().add(new BracketTeam(raw.trim()));
            }
        }
        Collections.shuffle(state.getTeams());
    }

    public synchronized void resetBracket() {
        state.getTeams().clear();
        state.getMatches().clear();
        state.setCurrentTeamAId(null);
        state.setCurrentTeamBId(null);
        state.setFinished(false);
        state.setSuggestedWinnersTeamAId(null);
        state.setSuggestedWinnersTeamBId(null);
        state.setSuggestedLosersTeamAId(null);
        state.setSuggestedLosersTeamBId(null);
        state.getSuggestedWinnersPairs().clear();
        state.getSuggestedLosersPairs().clear();
        currentTeamAByGame.clear();
        currentTeamBByGame.clear();
    }

    public synchronized void setCurrentMatch(String teamAId, String teamBId, String gameId) {
        state.setCurrentTeamAId(teamAId);
        state.setCurrentTeamBId(teamBId);
        currentTeamAByGame.put(gameId, teamAId);
        currentTeamBByGame.put(gameId, teamBId);

        BracketTeam teamA = findTeam(teamAId);
        BracketTeam teamB = findTeam(teamBId);

        if (teamA != null && teamB != null) {
            gameService.setTeamNames(gameId, teamA.getName(), teamB.getName());
            String bracketName = (teamA.getLosses() == 0 && teamB.getLosses() == 0) ? "WINNERS" : "LOSERS";

            BracketMatch existing = state.getMatches().stream()
                    .filter(m -> !m.isCompleted()
                            && ((teamAId.equals(m.getTeamAId()) && teamBId.equals(m.getTeamBId()))
                            || (teamAId.equals(m.getTeamBId()) && teamBId.equals(m.getTeamAId()))))
                    .findFirst()
                    .orElse(null);

            if (existing == null) {
                int round = 1;
                for (BracketMatch m : state.getMatches()) {
                    if (bracketName.equals(m.getBracket()) && m.getRound() >= round) {
                        round = m.getRound() + 1;
                    }
                }
                state.getMatches().add(new BracketMatch(bracketName, round, teamAId, teamBId));
            }
        }
    }

    public synchronized void finalizeCurrentMatch(String gameId) {
        String teamAId = currentTeamAByGame.getOrDefault(gameId, state.getCurrentTeamAId());
        String teamBId = currentTeamBByGame.getOrDefault(gameId, state.getCurrentTeamBId());
        if (teamAId == null || teamBId == null) {
            return;
        }

        BracketTeam teamA = findTeam(teamAId);
        BracketTeam teamB = findTeam(teamBId);
        if (teamA == null || teamB == null) {
            return;
        }

        GameState gameState = gameService.getState(gameId);
        int scoreA = gameState.getTeamAScore();
        int scoreB = gameState.getTeamBScore();
        if (scoreA == scoreB) {
            return;
        }

        String bracketName = (teamA.getLosses() == 0 && teamB.getLosses() == 0) ? "WINNERS" : "LOSERS";
        BracketMatch match = state.getMatches().stream()
                .filter(m -> !m.isCompleted()
                        && teamAId.equals(m.getTeamAId())
                        && teamBId.equals(m.getTeamBId()))
                .findFirst()
                .orElse(null);

        if (match == null) {
            int round = 1;
            for (BracketMatch m : state.getMatches()) {
                if (bracketName.equals(m.getBracket()) && m.getRound() >= round) {
                    round = m.getRound() + 1;
                }
            }
            match = new BracketMatch(bracketName, round, teamAId, teamBId);
            state.getMatches().add(match);
        }

        match.setScoreA(scoreA);
        match.setScoreB(scoreB);
        if (scoreA > scoreB) {
            match.setWinnerId(teamAId);
            match.setLoserId(teamBId);
            teamB.setLosses(teamB.getLosses() + 1);
            if (teamB.getLosses() >= 2) {
                teamB.setEliminated(true);
            }
        } else {
            match.setWinnerId(teamBId);
            match.setLoserId(teamAId);
            teamA.setLosses(teamA.getLosses() + 1);
            if (teamA.getLosses() >= 2) {
                teamA.setEliminated(true);
            }
        }
        match.setCompleted(true);

        updateFinishedFlag();
    }

    private void updateFinishedFlag() {
        long alive = state.getTeams().stream()
                .filter(t -> !t.isEliminated())
                .count();
        state.setFinished(alive <= 1 && !state.getTeams().isEmpty());
    }

    private void computeSuggestions() {
        state.setSuggestedWinnersTeamAId(null);
        state.setSuggestedWinnersTeamBId(null);
        state.setSuggestedLosersTeamAId(null);
        state.setSuggestedLosersTeamBId(null);
        state.getSuggestedWinnersPairs().clear();
        state.getSuggestedLosersPairs().clear();

        if (state.getTeams().isEmpty()) {
            return;
        }

        List<BracketTeam> winners = new ArrayList<>();
        List<BracketTeam> losers = new ArrayList<>();
        for (BracketTeam team : state.getTeams()) {
            if (team.isEliminated()) {
                continue;
            }
            if (team.getLosses() == 0) {
                winners.add(team);
            } else if (team.getLosses() == 1) {
                losers.add(team);
            }
        }

        Set<String> busyPairsW = new HashSet<>();
        Set<String> busyPairsL = new HashSet<>();
        for (BracketMatch m : state.getMatches()) {
            if (!m.isCompleted()) {
                if ("WINNERS".equals(m.getBracket())) {
                    busyPairsW.add(m.getTeamAId() + "-" + m.getTeamBId());
                    busyPairsW.add(m.getTeamBId() + "-" + m.getTeamAId());
                } else if ("LOSERS".equals(m.getBracket())) {
                    busyPairsL.add(m.getTeamAId() + "-" + m.getTeamBId());
                    busyPairsL.add(m.getTeamBId() + "-" + m.getTeamAId());
                }
            }
        }

        if (winners.size() >= 2) {
            Set<String> used = new HashSet<>();
            for (int i = 0; i < winners.size(); i++) {
                if (used.contains(winners.get(i).getId())) continue;
                for (int j = i + 1; j < winners.size(); j++) {
                    if (used.contains(winners.get(j).getId())) continue;
                    String key = winners.get(i).getId() + "-" + winners.get(j).getId();
                    if (!busyPairsW.contains(key)) {
                        state.getSuggestedWinnersPairs().add(new SuggestedPair(winners.get(i).getId(), winners.get(j).getId()));
                        used.add(winners.get(i).getId());
                        used.add(winners.get(j).getId());
                        break;
                    }
                }
            }
            if (!state.getSuggestedWinnersPairs().isEmpty()) {
                state.setSuggestedWinnersTeamAId(state.getSuggestedWinnersPairs().get(0).getTeamAId());
                state.setSuggestedWinnersTeamBId(state.getSuggestedWinnersPairs().get(0).getTeamBId());
            }
        }

        if (losers.size() >= 2) {
            Set<String> used = new HashSet<>();
            for (int i = 0; i < losers.size(); i++) {
                if (used.contains(losers.get(i).getId())) continue;
                for (int j = i + 1; j < losers.size(); j++) {
                    if (used.contains(losers.get(j).getId())) continue;
                    String key = losers.get(i).getId() + "-" + losers.get(j).getId();
                    if (!busyPairsL.contains(key)) {
                        state.getSuggestedLosersPairs().add(new SuggestedPair(losers.get(i).getId(), losers.get(j).getId()));
                        used.add(losers.get(i).getId());
                        used.add(losers.get(j).getId());
                        break;
                    }
                }
            }
            if (!state.getSuggestedLosersPairs().isEmpty()) {
                state.setSuggestedLosersTeamAId(state.getSuggestedLosersPairs().get(0).getTeamAId());
                state.setSuggestedLosersTeamBId(state.getSuggestedLosersPairs().get(0).getTeamBId());
            }
        }
    }

    private BracketTeam findTeam(String id) {
        if (id == null) {
            return null;
        }
        for (BracketTeam team : state.getTeams()) {
            if (id.equals(team.getId())) {
                return team;
            }
        }
        return null;
    }
}
