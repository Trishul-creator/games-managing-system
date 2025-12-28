package com.example.quizbowl;

public class SuggestedPair {
    private String teamAId;
    private String teamBId;

    public SuggestedPair() {
    }

    public SuggestedPair(String teamAId, String teamBId) {
        this.teamAId = teamAId;
        this.teamBId = teamBId;
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
}
