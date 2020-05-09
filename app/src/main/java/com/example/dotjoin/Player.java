package com.example.dotjoin;

public class Player {
    private String name,deviceToken;
    private int score,color,playerNumber,ready,active;


    public Player(String name, int color, int score,int playerNumber,String deviceToken,int ready,int active) {
        this.name = name;
        this.color = color;
        this.score = score;
        this.playerNumber = playerNumber;
        this.deviceToken=deviceToken;
        this.ready=ready;
        this.active=active;
    }

    public Player() {
    }

    //Getters

    public int getActive() {
        return active;
    }

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

    public void setActive(int active) {
        this.active = active;
    }

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
