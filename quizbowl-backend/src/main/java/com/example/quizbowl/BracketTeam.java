package com.example.quizbowl;

import java.util.UUID;

public class BracketTeam {
    private String id;
    private String name;
    private int losses;
    private boolean eliminated;

    public BracketTeam() {
    }

    public BracketTeam(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.losses = 0;
        this.eliminated = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }
}
