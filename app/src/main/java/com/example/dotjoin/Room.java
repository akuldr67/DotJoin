package com.example.dotjoin;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.dotjoin.Board;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Room {
    private String roomID;
    private Game game;
    private int host;
    private boolean isGameStarted;
    private String roomStartTime;
    private String gameStartTime;
    private ArrayList<Player> players;

    public Room() {
    }

    //Constructor to Host Room
    public Room(String name) {
        this.roomID = generateRoomID();
        this.host = 0;
        this.isGameStarted = false;
        this.game =null;
        this.roomStartTime = new Date().toString();
        this.gameStartTime = null;
        players=new ArrayList<>();
        Player player = new Player(name,0,0,0);
        players.add(player);
    }

//    //Constructor to Join Room
//    public Room(String roomID){
//        this.roomID = roomID;
//        this.host = 0;
//        this.game = null;
//        this.roomStartTime = new Date().toString();
//        this.gameStartTime = null;
//    }

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

    public ArrayList<Player> getPlayers() {
        return players;
    }

    //setters
    public void setGame(Game game) { this.game = game; }

    public void setHost(int host) { this.host = host; }

    public void setIsGameStarted() { this.isGameStarted = true; }

    public void setRoomStartTime(String roomStartTime) { this.roomStartTime = roomStartTime; }

    public void setGameStartTime(String gameStartTime) { this.gameStartTime = gameStartTime; }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
}
