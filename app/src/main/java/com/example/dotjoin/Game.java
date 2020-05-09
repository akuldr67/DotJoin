package com.example.dotjoin;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Vector;

public class Game {

    //*********************
    //*Declaring Variables*
    //*********************
    int lastEdgeUpdated,noOfPlayers,currentPlayer,totalBoxes,boxesMade;
    ArrayList<Player> players;
    ArrayList<Integer>newBoxNodes;
    Board board;

    //*******************
    //*Empty Constructor*
    //*******************
    public Game() {
    }

    //*********************************
    //*Constructor with all parameters*
    //*********************************
    public Game(int lastEdgeUpdated, int noOfPlayers,Board board,ArrayList<Player>players) {
        this.lastEdgeUpdated = lastEdgeUpdated;
        this.noOfPlayers = noOfPlayers;
        this.board=board;
        this.totalBoxes=board.getTotalBoxes();
        currentPlayer=0;
        boxesMade=0;
        this.players=players;
//        players=new Vector<Player>();
//        players.setSize(noOfPlayers);
//        int color=0;
//        for(int i=0;i<noOfPlayers;i++){
//            if(i==0)color=R.drawable.colour_box_blue;
//            else if(i==1)color=R.drawable.colour_box_red;
//            else if(i==2)color=R.drawable.colour_box_green;
//            else if(i==3)color=R.drawable.colour_box_yellow;
//            players.set(i,new Player("Player "+(i+1),color,0,i,""));
//        }
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

    public ArrayList<Player> getPlayers(){
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getTotalBoxes() {
        return totalBoxes;
    }

    public int getBoxesMade() {
        return boxesMade;
    }

    public ArrayList<Integer> getNewBoxNodes() {
        return newBoxNodes;
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

    public void setPlayers(ArrayList<Player> players){
        this.players=players;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setTotalBoxes(int totalBoxes) {
        this.totalBoxes = totalBoxes;
    }

    public void setBoxesMade(int boxesMade) {
        this.boxesMade = boxesMade;
    }

    public void setNewBoxNodes(ArrayList<Integer> newBoxNodes) {
        this.newBoxNodes = newBoxNodes;
    }

    //***************
    //*Miscellaneous*
    //***************


    //Function to increase score if the current player makes a box
    public void increaseScore(){
        players.get(currentPlayer).setScore(players.get(currentPlayer).getScore()+board.isBoxCompleted(lastEdgeUpdated).size());
        boxesMade=boxesMade+board.isBoxCompleted(lastEdgeUpdated).size();
    }

    //Check if game is completed
    public boolean gameCompleted(){
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

    public int previousPlayer(int playerNo){
        if(playerNo==0) return (noOfPlayers-1);
        else return (playerNo-1);
    }

    public void colourBox(Board board,int NodeNo, Context context, ConstraintLayout root){
        if(NodeNo<1) return;
        if(NodeNo%board.getColumns()==0) return;
        if(NodeNo>(board.getTotalNodes()-board.getColumns())) return;

        ImageView colorImage = new ImageView(context);
        colorImage.setTranslationZ(1f);
        colorImage.setVisibility(View.VISIBLE);
        ConstraintLayout.LayoutParams params;

        float[] cor = board.FindCoordinatesOfNode(NodeNo);

        colorImage.setImageResource(players.get(currentPlayer).getColor());

        params = new ConstraintLayout.LayoutParams((int) (board.getBoxLength()), (int) (board.getBoxLength()));

        colorImage.setX(cor[0]);
        colorImage.setY(cor[1]);

        root.addView(colorImage, params);
    }

    public String resultString(){
        //Finding Max
        int max=0;
        for(int i=0;i<noOfPlayers;i++){
            if(players.get(i).getScore()>max){
                max=players.get(i).getScore();
            }
        }
        //Finding Name of Winners
        Vector<String>winners=new Vector<String>();
        for(int i=0;i<noOfPlayers;i++){
            if(players.get(i).getScore()==max){
                winners.add(players.get(i).getName());
            }
        }

        if(winners.size()==1){
            return winners.get(0)+" won";
        }
        else{
            String ans="It's a tie between";
            for(int i=0;i<winners.size();i++){
                if(i==winners.size()-1)ans=ans+winners.get(i);
                else if(i==winners.size()-2)ans=ans+" "+winners.get(i)+" and ";
                else ans=ans+" "+winners.get(i)+",";
            }
            return ans;
        }

    }

}
//TODO
// Turn Auto Rotate Off

//TODO
// Set Main Activity Parent of others
