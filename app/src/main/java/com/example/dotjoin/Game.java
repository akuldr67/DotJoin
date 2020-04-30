package com.example.dotjoin;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

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
    public Game(int lastEdgeUpdated, int noOfPlayers, Vector<String> namesOfPlayers,Board board) {
        this.lastEdgeUpdated = lastEdgeUpdated;
        this.noOfPlayers = noOfPlayers;
        this.namesOfPlayers = namesOfPlayers;
        this.board=board;
        this.totalBoxes=board.getTotalBoxes();
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
        if(boxesMade==totalBoxes)
            return true;
        else
            return false;
    }

    //NextPlayersTurn
    public void nextTurn(){
        if(currentPlayer==noOfPlayers-1)
            currentPlayer=0;
        else
            currentPlayer=currentPlayer+1;
    }

    public void colourBox(int NodeNo, Context context, ConstraintLayout root){
        if(NodeNo<1) return;
        if(NodeNo%this.board.getColumns()==0) return;
        if(NodeNo>(this.board.getTotalNodes()-this.board.getColumns())) return;

        ImageView colorImage = new ImageView(context);
        colorImage.bringToFront();
        colorImage.setVisibility(View.VISIBLE);
        ConstraintLayout.LayoutParams params;

        float[] cor = this.board.FindCoordinatesOfNode(NodeNo);

        if(currentPlayer == 0)
            colorImage.setImageResource(R.drawable.colour_box_blue);
        else if(currentPlayer == 1)
            colorImage.setImageResource(R.drawable.colour_box_red);
        else if(currentPlayer == 2)
            colorImage.setImageResource(R.drawable.colour_box_green);
        else if(currentPlayer == 3)
            colorImage.setImageResource(R.drawable.colour_box_yellow);

        params = new ConstraintLayout.LayoutParams((int) (this.board.getBoxLength()*85 /100), (int) (this.board.getBoxLength()*85 /100));

        colorImage.setX(cor[0]+(this.board.getBoxLength()*15 /100));
        colorImage.setY(cor[1]+(this.board.getBoxLength()*15 /100));

        root.addView(colorImage, params);
    }
}
