package com.example.quizbowl;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private String teamAName = "Team A";
    private String teamBName = "Team B";
    private int teamAScore = 0;
    private int teamBScore = 0;
    private int questionNumber = 1;
    private String lastTossupWinner;
    private List<GameEvent> history = new ArrayList<>();

    public String getTeamAName() {
        return teamAName;
    }

    public void setTeamAName(String teamAName) {
        this.teamAName = teamAName;
    }

    public String getTeamBName() {
        return teamBName;
    }

    public void setTeamBName(String teamBName) {
        this.teamBName = teamBName;
    }

    public int getTeamAScore() {
        return teamAScore;
    }

    public void setTeamAScore(int teamAScore) {
        this.teamAScore = teamAScore;
    }

    public int getTeamBScore() {
        return teamBScore;
    }

    public void setTeamBScore(int teamBScore) {
        this.teamBScore = teamBScore;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getLastTossupWinner() {
        return lastTossupWinner;
    }

    public void setLastTossupWinner(String lastTossupWinner) {
        this.lastTossupWinner = lastTossupWinner;
    }

    public List<GameEvent> getHistory() {
        return history;
    }

    public void setHistory(List<GameEvent> history) {
        this.history = history;
    }
}
