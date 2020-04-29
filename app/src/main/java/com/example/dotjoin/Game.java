package com.example.dotjoin;

import java.util.Vector;

public class Game {

    //*********************
    //*Declaring Variables*
    //*********************
    int lastEdgeUpdated,noOfPlayers,currentPlayer,totalBoxes,boxesMade;
    Vector<String> namesOfPlayers;
    Vector<Integer> scoreBoard;
    Board board;

    //*******************
    //*Empty Constructor*
    //*******************
    public Game() {
    }

    //*********************************
    //*Constructor with all parameters*
    //*********************************
    public Game(int lastEdgeUpdated, int noOfPlayers, int totalBoxes,Vector<String> namesOfPlayers,Board board) {
        this.lastEdgeUpdated = lastEdgeUpdated;
        this.noOfPlayers = noOfPlayers;
        this.namesOfPlayers = namesOfPlayers;
        this.board=board;
        this.totalBoxes=totalBoxes;
        currentPlayer=0;
        boxesMade=0;
        //Initializing Empty ScoreBoard
        scoreBoard=new Vector<Integer>();
        scoreBoard.setSize(noOfPlayers);
        for(int i=0;i<noOfPlayers;i++){
            scoreBoard.set(i,0);
        }
    }

    //*********
    //*Getters*
    //*********

    public int getLastEdgeUpdated() {
        return lastEdgeUpdated;
    }

    public int getNoOfPlayers() {
        return noOfPlayers;
    }

    public Vector<String> getNamesOfPlayers() {
        return namesOfPlayers;
    }

    public Vector<Integer> getScoreBoard() {
        return scoreBoard;
    }

    public Board getBoard() {
        return board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    //*********
    //*Setters*
    //*********

    public void setLastEdgeUpdated(int lastEdgeUpdated) {
        this.lastEdgeUpdated = lastEdgeUpdated;
    }

    public void setNoOfPlayers(int noOfPlayers) {
        this.noOfPlayers = noOfPlayers;
    }

    public void setNamesOfPlayers(Vector<String> namesOfPlayers) {
        this.namesOfPlayers = namesOfPlayers;
    }

    public void setScoreBoard(Vector<Integer> scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    //***************
    //*Miscellaneous*
    //***************


    //Function to increase score if the current player makes a box
    public void increaseScore(){
        scoreBoard.set(currentPlayer,scoreBoard.get(currentPlayer)+board.isBoxCompleted(lastEdgeUpdated).size());
        boxesMade=boxesMade+board.isBoxCompleted(lastEdgeUpdated).size();
    }

    //Check if game is completed
    public boolean isGameCompleted(){
        if(boxesMade==totalBoxes){
            return true;
        }
        else{
            return false;
        }
    }

    //NextPlayersTurn
    public void nextTurn(){
        if(currentPlayer==noOfPlayers-1){
            currentPlayer=0;
        }
        else
            currentPlayer=currentPlayer+1;
    }
}
