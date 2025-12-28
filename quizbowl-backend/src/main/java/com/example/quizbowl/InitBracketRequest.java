package com.example.quizbowl;

import java.util.List;

public class InitBracketRequest {
    private List<String> teamNames;

    public List<String> getTeamNames() {
        return teamNames;
    }

    public void setTeamNames(List<String> teamNames) {
        this.teamNames = teamNames;
    }
}
