package com.example.quizbowl;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bracket_meta")
public class BracketMetaEntity {
    @Id
    private Long id = 1L;
    private String currentTeamAId;
    private String currentTeamBId;
    private boolean finished;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
