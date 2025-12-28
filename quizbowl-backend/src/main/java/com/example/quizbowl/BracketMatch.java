package com.example.quizbowl;

import java.util.UUID;

public class BracketMatch {
    private String id;
    private String bracket; // "WINNERS" or "LOSERS"
    private int round;
    private String teamAId;
    private String teamBId;
    private Integer scoreA;
    private Integer scoreB;
    private String winnerId;
    private String loserId;
    private boolean completed;

    public BracketMatch() {
    }

    public BracketMatch(String bracket, int round, String teamAId, String teamBId) {
        this.id = UUID.randomUUID().toString();
        this.bracket = bracket;
        this.round = round;
        this.teamAId = teamAId;
        this.teamBId = teamBId;
        this.completed = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBracket() {
        return bracket;
    }

    public void setBracket(String bracket) {
        this.bracket = bracket;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getTeamAId() {
        return teamAId;
    }

    public void setTeamAId(String teamAId) {
        this.teamAId = teamAId;
    }

    public String getTeamBId() {
        return teamBId;
    }

    public void setTeamBId(String teamBId) {
        this.teamBId = teamBId;
    }

    public Integer getScoreA() {
        return scoreA;
    }

    public void setScoreA(Integer scoreA) {
        this.scoreA = scoreA;
    }

    public Integer getScoreB() {
        return scoreB;
    }

    public void setScoreB(Integer scoreB) {
        this.scoreB = scoreB;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public String getLoserId() {
        return loserId;
    }

    public void setLoserId(String loserId) {
        this.loserId = loserId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
