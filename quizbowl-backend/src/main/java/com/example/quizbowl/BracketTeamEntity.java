package com.example.quizbowl;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bracket_team")
public class BracketTeamEntity {
    @Id
    private String id;
    private String name;
    private int losses;
    private boolean eliminated;

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
