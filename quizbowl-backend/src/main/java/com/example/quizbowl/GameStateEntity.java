package com.example.quizbowl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "game_state")
public class GameStateEntity {
    @Id
    @Column(nullable = false, unique = true)
    private String gameId;

    private String teamAName;
    private String teamBName;
    private int teamAScore;
    private int teamBScore;
    private int questionNumber;
    private String lastTossupWinner;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

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
}
