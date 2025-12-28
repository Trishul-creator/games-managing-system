package com.example.quizbowl;

public class GameEvent {
    private String type;
    private String description;
    private long timestamp;
    private String team;
    private Integer points;

    public GameEvent() {
    }

    public GameEvent(String type, String description, String team, Integer points) {
        this.type = type;
        this.description = description;
        this.team = team;
        this.points = points;
        this.timestamp = System.currentTimeMillis();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
