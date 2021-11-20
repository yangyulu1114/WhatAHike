package com.ebookfrenzy.whatahike;

public class trailRecord {
    private String name;
    private String description;
    private String id;
    private int difficulty;

    public trailRecord(String name, String description, String id, int difficulty) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
