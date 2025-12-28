package com.example.quizbowl;

import java.util.ArrayList;
import java.util.List;

public class BracketState {
    private List<BracketTeam> teams = new ArrayList<>();
    private List<BracketMatch> matches = new ArrayList<>();
    private String currentTeamAId;
    private String currentTeamBId;
    private boolean finished;

    private String suggestedWinnersTeamAId;
    private String suggestedWinnersTeamBId;
    private String suggestedLosersTeamAId;
    private String suggestedLosersTeamBId;
    private List<SuggestedPair> suggestedWinnersPairs = new ArrayList<>();
    private List<SuggestedPair> suggestedLosersPairs = new ArrayList<>();

    public List<BracketTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<BracketTeam> teams) {
        this.teams = teams;
    }

    public List<BracketMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<BracketMatch> matches) {
        this.matches = matches;
    }

    public String getCurrentTeamAId() {
        return currentTeamAId;
    }

    public void setCurrentTeamAId(String currentTeamAId) {
        this.currentTeamAId = currentTeamAId;
    }

    public String getCurrentTeamBId() {
        return currentTeamBId;
    }

    public void setCurrentTeamBId(String currentTeamBId) {
        this.currentTeamBId = currentTeamBId;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getSuggestedWinnersTeamAId() {
        return suggestedWinnersTeamAId;
    }

    public void setSuggestedWinnersTeamAId(String suggestedWinnersTeamAId) {
        this.suggestedWinnersTeamAId = suggestedWinnersTeamAId;
    }

    public String getSuggestedWinnersTeamBId() {
        return suggestedWinnersTeamBId;
    }

    public void setSuggestedWinnersTeamBId(String suggestedWinnersTeamBId) {
        this.suggestedWinnersTeamBId = suggestedWinnersTeamBId;
    }

    public String getSuggestedLosersTeamAId() {
        return suggestedLosersTeamAId;
    }

    public void setSuggestedLosersTeamAId(String suggestedLosersTeamAId) {
        this.suggestedLosersTeamAId = suggestedLosersTeamAId;
    }

    public String getSuggestedLosersTeamBId() {
        return suggestedLosersTeamBId;
    }

    public void setSuggestedLosersTeamBId(String suggestedLosersTeamBId) {
        this.suggestedLosersTeamBId = suggestedLosersTeamBId;
    }

    public List<SuggestedPair> getSuggestedWinnersPairs() {
        return suggestedWinnersPairs;
    }

    public void setSuggestedWinnersPairs(List<SuggestedPair> suggestedWinnersPairs) {
        this.suggestedWinnersPairs = suggestedWinnersPairs;
    }

    public List<SuggestedPair> getSuggestedLosersPairs() {
        return suggestedLosersPairs;
    }

    public void setSuggestedLosersPairs(List<SuggestedPair> suggestedLosersPairs) {
        this.suggestedLosersPairs = suggestedLosersPairs;
    }
}
