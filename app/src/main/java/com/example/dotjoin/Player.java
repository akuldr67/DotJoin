package com.example.dotjoin;

public class Player {
    private String name,deviceToken;
    private int score,color,playerNumber,ready;


    public Player(String name, int color, int score,int playerNumber,String deviceToken,int ready) {
        this.name = name;
        this.color = color;
        this.score = score;
        this.playerNumber = playerNumber;
        this.deviceToken=deviceToken;
        this.ready=ready;
    }

    public Player() {
    }

    //Getters


    public int getReady() {
        return ready;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

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


    public void setReady(int ready) {
        this.ready = ready;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

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
