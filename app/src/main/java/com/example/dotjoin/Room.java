package com.example.dotjoin;

import android.util.Log;

import com.example.dotjoin.Board;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class Room {
    public String roomID;
    public Game game;
    public int host;
    public boolean isGameStarted;
    public String roomStartTime;
    public String gameStartTime;


    //Constructor to Host Room
    public Room() {
        this.roomID = generateRoomID();
        this.host = 0;
        this.isGameStarted = false;
//        Board b = new Board(3,3,100,100,0);
//        this.game = new Game(0,2,b);
        this.game = null;
        this.roomStartTime = new Date().toString();
        this.gameStartTime = null;
    }

    //Constructor to Join Room
    public Room(String roomID){
        this.roomID = roomID;
        this.host = 0;
        this.game = null;
        this.roomStartTime = new Date().toString();
        this.gameStartTime = null;
    }

    private String generateRoomID(){
        String id="";
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        for(int i=0;i<8;i++){
            int index = (int)(CHAR_LOWER.length()*Math.random());
            id+=CHAR_LOWER.charAt(index);
        }
        return id;
    }


    //getters
    public String getRoomID() { return this.roomID; }

    public Game getGame() { return this.game; }

    public int getHost() { return this.host; }

    public boolean getIsGameStarted() { return isGameStarted; }

    public String getRoomStartTime() { return this.roomStartTime; }

    public String getGameStartTime(){ return this.gameStartTime; }



    //setters
    public void setGame(Game game) { this.game = game; }

    public void setHost(int host) { this.host = host; }

    public void setIsGameStarted() { this.isGameStarted = true; }

    public void setRoomStartTime(String roomStartTime) { this.roomStartTime = roomStartTime; }

    public void setGameStartTime(String gameStartTime) { this.gameStartTime = gameStartTime; }
}
