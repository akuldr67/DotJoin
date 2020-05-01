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
    public Date RoomstartTime,GameStartTime;


    public Room() {
        this.roomID = generateRoomID();
        this.host = 0;
        this.isGameStarted = false;
        this.RoomstartTime = new Date();
    }

    private String generateRoomID(){
        String id="";
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        for(int i=0;i<8;i++){
            int index = (int)(CHAR_LOWER.length()*Math.random());
            id+=CHAR_LOWER.charAt(index);
        }
        Log.d("Room id ", " : "+id);
        return id;
    }


    //getters
    public String getRoomID() {
        return this.roomID;
    }

    public Game getGame() {
        return this.game;
    }

    public int getHost() {
        return this.host;
    }

    public boolean isGameStarted() {
        return this.isGameStarted;
    }

    public Date getRoomStartTime() {
        return this.RoomstartTime;
    }

    public Date getGameStartTime(){
        return this.GameStartTime;
    }

    //setters
    public void setGame(Game game) {
        this.game = game;
    }

    public void setHost(int host) {
        this.host = host;
    }

    public void startGame() {
        this.isGameStarted = true;
    }

}
