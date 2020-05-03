package com.example.dotjoin;

import java.util.Vector;

public class BoardHelperForAI {

    Board board;
    private Vector<Boolean> edges;
    private Vector<Boolean> boxes;
    private int rows,columns,totalEdges,totalBoxes,totalNodes;

    public BoardHelperForAI(Board board) {
        this.board = board;
        this.edges = board.getEdges();
        this.boxes = board.getBoxes();
        this.rows = board.getRows();
        this.columns = board.getColumns();
        this.totalBoxes = board.getTotalBoxes();
        this.totalEdges = board.getTotalEdges();
        this.totalNodes = board.getTotalNodes();
    }

    public int giveNextEdgeNoEasy(){
        for(int i=1;i<this.totalEdges+1;i++){
            if(!edges.get(i))return i;
        }
        return -1;
    }

    public int giveNextEdgeNoHard(){
        for(int i=1;i<this.totalEdges+1;i++){
            if(!edges.get(i))return i;
        }
        return -1;
    }

    public Board getBoard() { return board; }

    public void setBoard(Board board) { this.board = board; }
}
