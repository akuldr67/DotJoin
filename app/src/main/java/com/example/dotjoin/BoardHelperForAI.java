package com.example.dotjoin;

import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

public class BoardHelperForAI {

    Board board;
    private ArrayList<Boolean> edges;
    private ArrayList<Boolean> boxes;
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
        int edgeNo = findCompletingEdge();
        if(edgeNo!=-1)return edgeNo;
        Vector<Integer> safeEdges = findSafeEdges();
        if(safeEdges.size()==0) {
            for (int i = 1; i < this.totalEdges + 1; i++) {
                if (!this.edges.get(i)) return i;
            }
        }else {
            int index = (int)(safeEdges.size()*Math.random());
            return safeEdges.get(index);
        }
        return -1;
    }

    public int giveNextEdgeNoHard(){
        int edgeNo = findCompletingEdge();
        if(edgeNo!=-1)return edgeNo;
        Vector<Integer> safeEdges = findSafeEdges();
        if(safeEdges.size()==0) {
            for (int i = 1; i < this.totalEdges + 1; i++) {
                if (!this.edges.get(i)) return i;
            }
        }else {
            int index = (int)(safeEdges.size()*Math.random());
            return safeEdges.get(index);
        }
        return -1;
    }

    public Vector<Integer> EdgesFromBoxNo(int boxNo){
        if(boxNo<1 || boxNo>this.totalBoxes) return null;
        int nodeNo = this.board.NodeNoGivenBoxNo(boxNo);
        Vector<Integer> ans = new Vector<>();
        ans.add(this.board.horEdgeGivenNode(nodeNo));
        ans.add(this.board.verEdgeGivenNode(nodeNo));
        ans.add(ans.get(1)+1);
        ans.add(ans.get(1)+this.columns);
        return ans;
    }

    public int isThreeSidesMade(int boxNo){
        if(boxNo<1 || boxNo>this.totalBoxes) return -1;
        Vector<Integer> boxEdges = EdgesFromBoxNo(boxNo);
        int count=0,ans=-1;
        for(int i=0;i<4;i++){
            if(this.edges.get(boxEdges.get(i)))count++;
            else ans = boxEdges.get(i);
        }
        if(count==3) return ans;
        else return -1;
    }

    public int findCompletingEdge(){
        for(int i=1;i<this.totalBoxes+1;i++){
            if(!this.boxes.get(i)){
                int edgeNo = isThreeSidesMade(i);
                if(edgeNo!=-1) return edgeNo;
            }
        }
        return -1;
    }

    public Vector<Integer> findSafeEdges(){
        Vector<Integer> safeEdges = new Vector<>();
        for(int i=1;i<this.totalEdges+1;i++){
            if(!this.edges.get(i)){
                boolean flag1 = true, flag2 = true;
                if(this.board.isEdgeNoHorizontal(i)){
                    if(this.board.ifTopBoxExist(i)){
                        int count = 0;
                        if(this.edges.get(i-this.columns))count++;
                        if(this.edges.get(i-this.columns+1))count++;
                        if(this.edges.get(i-(2*this.columns)+1))count++;
                        if(count>1)flag1 = false;
                    }
                    if(this.board.ifBottomBoxExist(i)){
                        int count = 0;
                        if(this.edges.get(i+this.columns-1))count++;
                        if(this.edges.get(i+this.columns))count++;
                        if(this.edges.get(i+(2*this.columns)-1))count++;
                        if(count>1)flag2 = false;
                    }
                }
                else{
                    if(this.board.ifLeftBoxExist(i)){
                        int count = 0;
                        if(this.edges.get(i-1))count++;
                        if(this.edges.get(i-this.columns))count++;
                        if(this.edges.get(i+this.columns-1))count++;
                        if(count>1)flag1 = false;
                    }
                    if(this.board.ifRightBoxExist(i)){
                        int count = 0;
                        if(this.edges.get(i+1))count++;
                        if(this.edges.get(i-this.columns+1))count++;
                        if(this.edges.get(i+this.columns))count++;
                        if(count>1)flag2 = false;
                    }
                }
                if(flag1 && flag2) safeEdges.add(i);
            }
        }
        return safeEdges;
    }

    public Board getBoard() { return board; }

    public void setBoard(Board board) { this.board = board; }
}
