package com.example.dotjoin;

public class Player {
    private String name;
    private int score,color;

    public Player(String name, int color, int score) {
        this.name = name;
        this.color = color;
        this.score = score;
    }

    public Player() {
    }

    //Getters

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public int getScore() {
        return score;
    }

    //Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
