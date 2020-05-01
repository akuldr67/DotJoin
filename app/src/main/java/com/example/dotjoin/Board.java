package com.example.dotjoin;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Arrays;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;
import java.util.Vector;

public class Board {
    //          Board status
    //      *----*----*        1   2
    //      |    |    |      3   4   5
    //      *----*----*        6   7
    //      |    |    |      8   9   10
    //      *----*----*       11   12

    private int rows,columns,totalEdges,totalBoxes,totalNodes;
    private float boxLength,dotDia,buffer,gridTopLeftX,gridTopLeftY,gridMarginX,gridMarginY;
    private float firstNodeX, firstNodeY;
    private float gridFirstCol, gridLastCol, gridFirstRow, gridLastRow;
    private boolean horizontalEdge = false, verticalEdge = false;
    boolean[] edges;
    boolean[] boxes;


    public Board(int rows,int columns,float gridTopLeftX,float gridTopLeftY,float width){
        this.rows=rows;
        this.columns=columns;
        this.gridTopLeftX =gridTopLeftX;
        this.gridTopLeftY =gridTopLeftY;
        LayoutUtils layoutUtils = new LayoutUtils();
        this.dotDia=layoutUtils.getDiaOfDot(width,columns);
        this.boxLength=layoutUtils.getBoxLength(width,columns)-this.dotDia;
        this.buffer=layoutUtils.getBuffer(columns);
        this.totalEdges = this.totalNoOfEdges();
        edges = new boolean[this.totalEdges + 1];
        this.totalBoxes = this.totalNoOfBoxes();
        boxes = new boolean[this.totalBoxes + 1];
        this.totalNodes = this.totalNoOfNodes();
        this.gridMarginX=layoutUtils.getDiaOfDot(width, columns)/2;
        this.gridMarginY=layoutUtils.getDiaOfDot(width,columns)/2;
        setGridDimensions();
    }

    public Board(){

    }


    public void setFirstNodeCor(){
        this.firstNodeX = this.gridTopLeftX + this.gridMarginX;
        this.firstNodeY = this.gridTopLeftY + this.gridMarginY;
    }

    public void setGridDimensions(){
        this.setFirstNodeCor();
        this.gridFirstCol = this.firstNodeX;
        this.gridFirstRow = this.firstNodeY;
        float gridLengthX = ((this.columns-1)*(this.boxLength));
        float gridLengthY = ((this.rows-1)*(this.boxLength));
        this.gridLastCol = this.gridFirstCol + gridLengthX;
        this.gridLastRow = this.gridFirstRow + gridLengthY;
    }

    public int totalNoOfNodes(){
        int totalNodes = this.rows*this.columns;
        return totalNodes;
    }

    public int totalNoOfBoxes(){
        int totalBoxes = (this.columns-1)*(this.rows-1);
        return totalBoxes;
    }

    public int totalNoOfEdges(){
        int totalEdges = (2*this.rows*this.columns)-(this.columns+this.rows);
        return totalEdges;
    }

    public boolean isClickInsideGrid(float x, float y){
        if(x >= (this.gridFirstCol-this.buffer) && x<= (this.gridLastCol+this.buffer)){
            if(y >= (this.gridFirstRow-this.buffer) && y<=this.gridLastRow+this.buffer)
                return true;
        }
        return false;
    }

    public int NodeNoGivenRowCol(int rowNo, int colNo){
        int NodeNo = (this.columns*(rowNo-1)) + colNo;
        return NodeNo;
    }

    public int NodeNoGivenCor(float x, float y){
        int NodeNumber = -1;
        if(!this.isClickInsideGrid(x,y)) return -1;
        boolean onColumn = false, onRow = false;
        int ColumnNo = 1, RowNo = 1;
        this.verticalEdge = false;
        this.horizontalEdge = false;

        float relX = x-this.gridFirstCol;
        float tempX = relX % this.boxLength;
        if(tempX <= this.buffer){
            onColumn=true;
            ColumnNo = (int)(relX/this.boxLength)+1;
        }
        else if(Math.abs(tempX-this.boxLength)<=this.buffer){
            onColumn=true;
            ColumnNo = (int)(relX/this.boxLength)+2;
        }

        float relY = y-this.gridFirstRow;
        float tempY = relY % this.boxLength;
        if(tempY <= this.buffer){
            onRow=true;
            RowNo = (int)(relY/this.boxLength)+1;
        }
        else if(Math.abs(relY-this.boxLength)<=this.buffer){
            onRow=true;
            RowNo = (int)(relY/this.boxLength)+2;
        }

        //Preference Column > Row... means vertical edge > horizontal edge
        // Can change.. if instead of else below changed to if(onRow && NodeNumber == -1) .. also change in fn edgeNoGivenCor.
        // also can add possible change: in if below: if(NodeNumber!=-1) this.verticalEdge = true;

        if(onColumn){
            int RowNumber;
            relY = y-this.gridFirstRow;
            RowNumber = (int)(relY/this.boxLength)+1;
            this.verticalEdge = true;
            NodeNumber = NodeNoGivenRowCol(RowNumber,ColumnNo);
        }

        else if(onRow){
            int ColNumber;
            relX = x-this.gridFirstCol;
            ColNumber = (int)(relX/this.boxLength)+1;
            this.horizontalEdge = true;
            NodeNumber = NodeNoGivenRowCol(RowNo,ColNumber);
        }

        return NodeNumber;
    }

    public int horEdgeGivenNode(int NodeNo){
        if(NodeNo % this.columns == 0) return -1;
        int horizontalBefore = (this.columns-1)*(NodeNo/this.columns);
        int verticalBefore = this.columns*(NodeNo/this.columns);
        int remaining = NodeNo % this.columns;
        return (horizontalBefore + verticalBefore + remaining);
    }

    public  int verEdgeGivenNode(int NodeNo){
        if(NodeNo>((this.rows*this.columns)-this.columns)) return -1;
        if(NodeNo%this.columns == 0){
            int horizontalBefore = (this.columns-1)*(NodeNo/this.columns);
            int verticalBefore = this.columns*((NodeNo/this.columns)-1);
            int remaining = this.columns;
            return (horizontalBefore + verticalBefore + remaining);
        }
        else{
            int horizontalBefore = (this.columns-1)*((NodeNo/this.columns)+1);
            int verticalBefore = this.columns*(NodeNo/this.columns);
            int remaining = NodeNo % this.columns;
            return (horizontalBefore + verticalBefore + remaining);
        }
    }

    public int EdgeNoGivenCor(float x, float y){
        int NodeNumber = NodeNoGivenCor(x,y);
        if(NodeNumber == -1) return -1;
        if(this.horizontalEdge) return horEdgeGivenNode(NodeNumber);
        else if(this.verticalEdge) return verEdgeGivenNode(NodeNumber);
        return -1;
    }

    public boolean isEdgeNoHorizontal(int EdgeNo){
        int temp = (EdgeNo%((2*this.columns) - 1));
        if(temp == 0) return false;
        if(temp<this.columns) return true;
        else return false;
    }

    public Pair<Float,Float> findEdgeCordinates(int EdgeNo){
        float xCorStart,yCorStart,xCorEnd,yCorEnd,midx,midy;
        if(EdgeNo<1 || EdgeNo>this.totalEdges) return null;

        if(isEdgeNoHorizontal(EdgeNo)){
            int rowNo = (EdgeNo/((2*this.columns)-1));
            yCorStart = this.gridFirstRow + (rowNo*(this.boxLength));
            yCorEnd = yCorStart;
            int EdgeNoInThatRow = (EdgeNo%((2*this.columns)-1))-1;
            xCorStart = this.gridFirstCol + (EdgeNoInThatRow*(this.boxLength));
            xCorEnd = xCorStart + this.boxLength;
        }
        else{
            int rowNoStart = (EdgeNo/((2*this.columns)-1));
            if((EdgeNo%((2*this.columns)-1))==0)rowNoStart--;
            yCorStart = this.gridFirstRow + (rowNoStart*(this.boxLength));
            yCorEnd = yCorStart + this.boxLength;
            int colNo = (EdgeNo%((2*this.columns)-1))-this.columns;
            if((EdgeNo%((2*this.columns)-1))==0)colNo=this.columns-1;
            xCorStart = this.gridFirstCol + (colNo*(this.boxLength));
            xCorEnd = xCorStart;
        }

        Pair<Float,Float> EdgeCordinates= new Pair<Float,Float>(xCorStart,yCorStart);
        return EdgeCordinates;
    }

    public void placeEdgeGivenEdgeNo(int EdgeNo, Context context, ConstraintLayout root){
        if(EdgeNo!=-1) {
            ImageView lineImage = new ImageView(context);
            lineImage.bringToFront();
            lineImage.setTranslationZ(2f);
            lineImage.setVisibility(View.VISIBLE);
            ConstraintLayout.LayoutParams params;
            Pair<Float, Float> EdgeCordinates = findEdgeCordinates(EdgeNo);

            if (isEdgeNoHorizontal(EdgeNo)) {
                lineImage.setImageResource(R.drawable.horizontalline1);
                params = new ConstraintLayout.LayoutParams((int) (boxLength+dotDia), (int) dotDia);
                lineImage.setX(EdgeCordinates.first-(dotDia/2));
                lineImage.setY(EdgeCordinates.second-(dotDia/2));
            } else {
                lineImage.setImageResource(R.drawable.verticalline1);
                params = new ConstraintLayout.LayoutParams((int) dotDia, (int)(boxLength+dotDia));
                lineImage.setX(EdgeCordinates.first-(dotDia/2));
                lineImage.setY(EdgeCordinates.second-(dotDia/2));
            }
            root.addView(lineImage, params);
        }
    }

    public void makeMoveAt(int EdgeNo){
        if(EdgeNo!=-1)
            edges[EdgeNo]=true;
    }

    public void placeEdgesAccToGame(Context context, ConstraintLayout root){
        for(int i=1;i<=this.totalEdges;i++){
            if(edges[i]==true){
                placeEdgeGivenEdgeNo(i,context, root);
            }
        }
    }

    //it assumes old game sticks available
    public void updateGame(Context context, float newTapX, float newTapY, ConstraintLayout root){
        int newEdgeNo = this.EdgeNoGivenCor(newTapX,newTapY);
        if(newEdgeNo <=0 || newEdgeNo>this.totalEdges) return;
        if(edges[newEdgeNo]==true) return;
        placeEdgeGivenEdgeNo(newEdgeNo, context, root);
        edges[newEdgeNo]=true;
    }

    //deploys every valid stick at every step
    public void updateGame2(Context context, float newTapX, float newTapY, ConstraintLayout root){
        int newEdgeNo = this.EdgeNoGivenCor(newTapX,newTapY);
        if(newEdgeNo <=0 || newEdgeNo>this.totalEdges) return;
        if(edges[newEdgeNo]==true) return;
        edges[newEdgeNo]=true;
        placeEdgesAccToGame(context, root);
    }

    public int NodeNoGivenEdgeNo(int EdgeNo){
        if(EdgeNo<1 || EdgeNo>this.totalEdges) return -1;

        if(isEdgeNoHorizontal(EdgeNo)){
            int rowNo = (EdgeNo/((2*this.columns)-1)) + 1;
            int colNo = EdgeNo%((2*this.columns)-1);
            return NodeNoGivenRowCol(rowNo,colNo);
        }
        else {
            int colNo = (EdgeNo%((2*this.columns)-1))-this.columns + 1;
            if((EdgeNo%((2*this.columns)-1))==0)colNo=this.columns;
            int rowNo = (EdgeNo/((2*this.columns)-1))+1;
            if((EdgeNo%((2*this.columns)-1))==0)rowNo--;
            return  NodeNoGivenRowCol(rowNo,colNo);
        }
    }

    //a top left node represents its box!!

    public int BoxNoGivenNodeNo(int NodeNo){
        if(NodeNo<1) return -1;
        if(NodeNo%this.columns==0) return -1;
        if(NodeNo>((this.columns*this.rows)-this.columns)) return -1;
        int rowNo = (NodeNo/this.columns)+1;
        int colNo = NodeNo%this.columns;
        return ((rowNo-1)*(this.columns-1))+colNo;
    }

    public int NodeNoGivenBoxNo(int BoxNo){
        int rowNo = (BoxNo/(this.columns-1))+1;
        if(BoxNo%(this.columns-1)==0)rowNo--;
        int colNo = BoxNo%(this.columns-1);
        if(BoxNo%(this.columns-1)==0) colNo=this.columns-1;
        return NodeNoGivenRowCol(rowNo,colNo);
    }

    public boolean ifLeftBoxExist(int EdgeNo){
        if(EdgeNo<1 || EdgeNo>this.totalEdges) return false;
        int nodeNo = NodeNoGivenEdgeNo(EdgeNo);
        if((nodeNo-1)%this.columns==0) return false;
        return true;
    }

    public boolean ifRightBoxExist(int EdgeNo){
        if(EdgeNo<1 || EdgeNo>this.totalEdges) return false;
        int nodeNo = NodeNoGivenEdgeNo(EdgeNo);
        if(nodeNo%this.columns==0) return false;
        return true;
    }

    public boolean ifTopBoxExist(int EdgeNo){
        if(EdgeNo<this.columns || EdgeNo>this.totalEdges) return false;
        return true;
    }

    public boolean ifBottomBoxExist(int EdgeNo){
        if(EdgeNo<1 || EdgeNo>(this.totalEdges-this.columns)) return false;
        return true;
    }

    public Vector<Integer> isBoxCompleted(int EdgeNo){
        int nodeNo = NodeNoGivenEdgeNo(EdgeNo);
        int currBoxNo = BoxNoGivenNodeNo(nodeNo);
        Vector<Integer> newBoxes = new Vector();

        if(isEdgeNoHorizontal(EdgeNo)){             //if new edge is horizontal.. means top and bottom box possible
            if(ifTopBoxExist(EdgeNo)){
                if(edges[EdgeNo-this.columns] && edges[EdgeNo-this.columns+1] && edges[EdgeNo-(2*this.columns)+1]){
                    if(currBoxNo == -1)currBoxNo = BoxNoGivenNodeNo(nodeNo-this.columns)+this.columns-1;
                    boxes[currBoxNo-this.columns+1] = true;
                    newBoxes.add(currBoxNo-this.columns+1);
                }
            }
            if(ifBottomBoxExist(EdgeNo)){
                if(edges[EdgeNo+this.columns-1] && edges[EdgeNo+this.columns] && edges[EdgeNo+(2*this.columns)-1]){
                    boxes[currBoxNo] = true;
                    newBoxes.add(currBoxNo);
                }
            }
        }
        else{                                     //if new edge is vertical.. means left and right box possible
            if(ifLeftBoxExist(EdgeNo)){
                if(edges[EdgeNo-1] && edges[EdgeNo-this.columns] && edges[EdgeNo+this.columns-1]){
                    if(currBoxNo == -1) currBoxNo = BoxNoGivenNodeNo(nodeNo-1)+1;
                    boxes[currBoxNo-1] = true;
                    newBoxes.add(currBoxNo-1);
                }
            }
            if(ifRightBoxExist(EdgeNo)){
                if(edges[EdgeNo+1] && edges[EdgeNo-this.columns+1] && edges[EdgeNo+this.columns]){
                    boxes[currBoxNo] = true;
                    newBoxes.add(currBoxNo);
                }
            }
        }

        Log.d("New ","Box No: "+newBoxes.toString());
        Vector<Integer> newBoxNodes = new Vector();
        for(int i=0;i<newBoxes.size();i++){
            newBoxNodes.add(NodeNoGivenBoxNo(newBoxes.get(i)));
        }
        return newBoxNodes;
    }

    public float[] FindCoordinatesOfNode(int NodeNo){
        if(NodeNo<1 || NodeNo>this.totalNodes) return null;
        int rowNo = NodeNo/this.columns +1;
        if(NodeNo%this.columns==0) rowNo--;
        int colNo = NodeNo%this.columns;
        if(NodeNo%this.columns==0) colNo=this.columns;
        float x = this.gridFirstCol + (this.boxLength*(colNo-1));
        float y = this.gridFirstRow + (this.boxLength*(rowNo-1));
        float[] coordinates = {x,y};
        return coordinates;
    }

    public int getTotalEdges(){ return this.totalEdges; }

    public int getTotalBoxes(){ return this.totalBoxes; }

    public int getTotalNodes(){ return this.totalNodes; }

    public boolean[] getEdgesArray(){ return this.edges; }

    public int getRows(){ return this.rows; }

    public int getColumns(){ return this.columns; }

    public float getBoxLength(){ return this.boxLength; }

}
