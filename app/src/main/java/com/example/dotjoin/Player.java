package com.example.dotjoin;

public class Player {
    private String name;
    private int score,color,playerNumber;

    public Player(String name, int color, int score,int playerNumber) {
        this.name = name;
        this.color = color;
        this.score = score;
        this.playerNumber = playerNumber;
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

    public int getPlayerNumber() { return playerNumber; }

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

    public void setPlayerNumber(int playerNumber) { this.playerNumber = playerNumber; }
}
