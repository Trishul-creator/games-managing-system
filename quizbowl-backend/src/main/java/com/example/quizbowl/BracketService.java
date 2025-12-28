package com.example.quizbowl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class BracketService {
    private final GameService gameService;
    private final BracketTeamRepository teamRepo;
    private final BracketMatchRepository matchRepo;
    private final BracketMetaRepository metaRepo;

    public BracketService(GameService gameService, BracketTeamRepository teamRepo, BracketMatchRepository matchRepo, BracketMetaRepository metaRepo) {
        this.gameService = gameService;
        this.teamRepo = teamRepo;
        this.matchRepo = matchRepo;
        this.metaRepo = metaRepo;
    }

    private BracketMetaEntity meta() {
        return metaRepo.findById(1L).orElseGet(() -> metaRepo.save(new BracketMetaEntity()));
    }

    public synchronized BracketState getState() {
        BracketState state = new BracketState();
        state.setTeams(teamRepo.findAll().stream().map(this::toDtoTeam).toList());
        state.setMatches(matchRepo.findAll().stream().map(this::toDtoMatch).toList());
        BracketMetaEntity meta = meta();
        state.setCurrentTeamAId(meta.getCurrentTeamAId());
        state.setCurrentTeamBId(meta.getCurrentTeamBId());
        updateFinishedFlag(state, meta);
        computeSuggestions(state);
        state.setFinished(meta.isFinished());
        return state;
    }

    public synchronized void initBracket(List<String> teamNames) {
        teamRepo.deleteAll();
        matchRepo.deleteAll();
        BracketMetaEntity meta = meta();
        meta.setCurrentTeamAId(null);
        meta.setCurrentTeamBId(null);
        meta.setFinished(false);
        metaRepo.save(meta);
        if (teamNames == null) {
            return;
        }
        for (String raw : teamNames) {
            if (StringUtils.hasText(raw)) {
                BracketTeamEntity t = new BracketTeamEntity();
                t.setId(UUID.randomUUID().toString());
                t.setName(raw.trim());
                t.setLosses(0);
                t.setEliminated(false);
                teamRepo.save(t);
            }
        }
    }

    public synchronized void resetBracket() {
        teamRepo.deleteAll();
        matchRepo.deleteAll();
        BracketMetaEntity meta = meta();
        meta.setCurrentTeamAId(null);
        meta.setCurrentTeamBId(null);
        meta.setFinished(false);
        metaRepo.save(meta);
    }

    public synchronized void setCurrentMatch(String teamAId, String teamBId, String gameId) {
        BracketMetaEntity meta = meta();
        meta.setCurrentTeamAId(teamAId);
        meta.setCurrentTeamBId(teamBId);
        metaRepo.save(meta);

        BracketTeamEntity teamA = teamRepo.findById(teamAId).orElse(null);
        BracketTeamEntity teamB = teamRepo.findById(teamBId).orElse(null);

        if (teamA != null && teamB != null) {
            gameService.setTeamNames(gameId, teamA.getName(), teamB.getName());
            String bracketName = (teamA.getLosses() == 0 && teamB.getLosses() == 0) ? "WINNERS" : "LOSERS";

            BracketMatchEntity existing = matchRepo.findAll().stream()
                    .filter(m -> !m.isCompleted()
                            && ((teamAId.equals(m.getTeamAId()) && teamBId.equals(m.getTeamBId()))
                            || (teamAId.equals(m.getTeamBId()) && teamBId.equals(m.getTeamAId()))))
                    .findFirst()
                    .orElse(null);

            if (existing == null) {
                int round = 1;
                for (BracketMatchEntity m : matchRepo.findAll()) {
                    if (bracketName.equals(m.getBracket()) && m.getRound() >= round) {
                        round = m.getRound() + 1;
                    }
                }
                BracketMatchEntity nm = new BracketMatchEntity();
                nm.setId(UUID.randomUUID().toString());
                nm.setBracket(bracketName);
                nm.setRound(round);
                nm.setTeamAId(teamAId);
                nm.setTeamBId(teamBId);
                nm.setCompleted(false);
                matchRepo.save(nm);
            }
        }
    }

    public synchronized void finalizeCurrentMatch(String gameId) {
        BracketMetaEntity meta = meta();
        String teamAId = meta.getCurrentTeamAId();
        String teamBId = meta.getCurrentTeamBId();
        if (teamAId == null || teamBId == null) {
            return;
        }

        BracketTeamEntity teamA = teamRepo.findById(teamAId).orElse(null);
        BracketTeamEntity teamB = teamRepo.findById(teamBId).orElse(null);
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
        BracketMatchEntity match = matchRepo.findAll().stream()
                .filter(m -> !m.isCompleted()
                        && teamAId.equals(m.getTeamAId())
                        && teamBId.equals(m.getTeamBId()))
                .findFirst()
                .orElse(null);

        if (match == null) {
            int round = 1;
            for (BracketMatchEntity m : matchRepo.findAll()) {
                if (bracketName.equals(m.getBracket()) && m.getRound() >= round) {
                    round = m.getRound() + 1;
                }
            }
            match = new BracketMatchEntity();
            match.setId(UUID.randomUUID().toString());
            match.setBracket(bracketName);
            match.setRound(round);
            match.setTeamAId(teamAId);
            match.setTeamBId(teamBId);
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
            teamRepo.save(teamB);
        } else {
            match.setWinnerId(teamBId);
            match.setLoserId(teamAId);
            teamA.setLosses(teamA.getLosses() + 1);
            if (teamA.getLosses() >= 2) {
                teamA.setEliminated(true);
            }
            teamRepo.save(teamA);
        }
        match.setCompleted(true);
        matchRepo.save(match);

        BracketState state = getState();
        updateFinishedFlag(state, meta());
    }

    private void updateFinishedFlag(BracketState state, BracketMetaEntity meta) {
        long alive = state.getTeams().stream()
                .filter(t -> !t.isEliminated())
                .count();
        meta.setFinished(alive <= 1 && !state.getTeams().isEmpty());
        metaRepo.save(meta);
    }

    private void computeSuggestions(BracketState state) {
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

    private BracketTeam toDtoTeam(BracketTeamEntity e) {
        BracketTeam t = new BracketTeam();
        t.setId(e.getId());
        t.setName(e.getName());
        t.setLosses(e.getLosses());
        t.setEliminated(e.isEliminated());
        return t;
    }

    private BracketMatch toDtoMatch(BracketMatchEntity e) {
        BracketMatch m = new BracketMatch();
        m.setId(e.getId());
        m.setBracket(e.getBracket());
        m.setRound(e.getRound());
        m.setTeamAId(e.getTeamAId());
        m.setTeamBId(e.getTeamBId());
        m.setScoreA(e.getScoreA());
        m.setScoreB(e.getScoreB());
        m.setWinnerId(e.getWinnerId());
        m.setLoserId(e.getLoserId());
        m.setCompleted(e.isCompleted());
        return m;
    }
}
