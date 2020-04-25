package com.example.dotjoin;

import com.example.dotjoin.Board;

import java.util.List;

public class Room {
    private String roomID;
    private List<String> playersList;
    private Board currBoardStatus;

    public Room() {
    }

    public Room(String roomID, List<String> playersList, Board currBoardStatus) {
        this.roomID = roomID;
        this.playersList = playersList;
        this.currBoardStatus = currBoardStatus;
    }

    public String getRoomID() {
        return roomID;
    }

    public List<String> getPlayersList() {
        return playersList;
    }

    public Board getCurrBoardStatus() {
        return currBoardStatus;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public void setPlayersList(List<String> playersList) {
        this.playersList = playersList;
    }

    public void setCurrBoardStatus(Board currBoardStatus) {
        this.currBoardStatus = currBoardStatus;
    }
}
