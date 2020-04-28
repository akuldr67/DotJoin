package com.example.dotjoin;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

public class Board {
    int size;
    //TODO Decide of how to store board status
    //      *----*----*        1   2
    //      |    |    |      3   4   5
    //      *----*----*        6   7
    //      |    |    |      8   9   10
    //      *----*----*       11   12

    // assumption: Edge Length = Box Length

    private int rows,columns;
    private float boxLength,buffer,gridTopLeftX,gridTopLeftY,gridMarginX,gridMarginY;
    private float firstNodeX, firstNodeY;
    private float gridFirstCol, gridLastCol, gridFirstRow, gridLastRow;
    private boolean horizontalEdge = false, verticalEdge = false;

//    float corX = 500, corY = 530;

    public Board(int rows,int columns,float gridTopLeftX,float gridTopLeftY,float width, float gridMarginX,float gridMarginY){
        this.rows=rows;
        this.columns=columns;
        this.gridTopLeftX =gridTopLeftX;
        this.gridTopLeftY =gridTopLeftY;
        this.boxLength=(width-(width/18))/(columns-1);
        this.gridMarginX=gridMarginX;
        this.gridMarginY=gridMarginY;
        this.buffer=40;
        setFirstNodeCor();
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
        float gridLengthX = this.columns*(this.boxLength-1);
        float gridLengthY = this.rows*(this.boxLength-1);
        this.gridLastCol = this.gridFirstCol + gridLengthX;
        this.gridLastRow = this.gridFirstRow + gridLengthY;
        Log.d("nfnalk","sdfssaas");
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
        float tempX = relX % this.boxLength;                       // modulus on float!!!
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

        if(onColumn){                                               //Preference Column > Row
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
        if(EdgeNo<1 || EdgeNo>totalNoOfEdges()) return null;

        if(isEdgeNoHorizontal(EdgeNo)){
            int rowNo = (EdgeNo/((2*this.columns)-1));
            yCorStart = this.gridFirstRow + (rowNo*this.boxLength);
            yCorEnd = yCorStart;
            int EdgeNoInThatRow = (EdgeNo%((2*this.columns)-1))-1;
            xCorStart = this.gridFirstCol + (EdgeNoInThatRow*this.boxLength);
            xCorEnd = xCorStart + this.boxLength;
        }
        else{
            int rowNoStart = (EdgeNo/((2*this.columns)-1));
            if((EdgeNo%((2*this.columns)-1))==0)rowNoStart--;
            yCorStart = this.gridFirstRow + (rowNoStart*this.boxLength);
            yCorEnd = yCorStart + this.boxLength;
            int colNo = (EdgeNo%((2*this.columns)-1))-this.columns;
            if((EdgeNo%((2*this.columns)-1))==0)colNo=this.columns-1;
            xCorStart = this.gridFirstCol + (colNo*this.boxLength);
            xCorEnd = xCorStart;
        }

        Pair<Float,Float> midCordinates= new Pair<Float,Float>(xCorStart,yCorStart);
        return midCordinates;
    }

    public void placeEdgeGivenEdgeNo(int EdgeNo, Context context, ConstraintLayout root){
        if(EdgeNo!=-1) {
            ImageView lineImage = new ImageView(context);
            lineImage.bringToFront();
            lineImage.setVisibility(View.VISIBLE);
            ConstraintLayout.LayoutParams params;
            Pair<Float, Float> EdgeCordinates = findEdgeCordinates(EdgeNo);

            Log.d("cor", "Box Length = " + boxLength);
            Log.d("cor", "x coordinate = " + EdgeCordinates.first);
            Log.d("cor", "y coordinate = " + EdgeCordinates.second);
            if (isEdgeNoHorizontal(EdgeNo)) {
                lineImage.setImageResource(R.drawable.horizontalline1);
                params = new ConstraintLayout.LayoutParams((int) boxLength, (int) (boxLength * 15 / 100));
                lineImage.setX(EdgeCordinates.first);
                lineImage.setY(EdgeCordinates.second);
//            lineImage.layout(Math.round(EdgeCordinates.first)-(int)(boxLength/2),Math.round(EdgeCordinates.second)-5,Math.round(EdgeCordinates.first)+(int)(boxLength/2),Math.round(EdgeCordinates.second)+5);
            } else {
                lineImage.setImageResource(R.drawable.verticalline1);
                params = new ConstraintLayout.LayoutParams((int) (boxLength * 15 / 100), (int) boxLength);
                lineImage.setX(EdgeCordinates.first);
                lineImage.setY(EdgeCordinates.second);
//            lineImage.layout(Math.round(EdgeCordinates.first)-5,Math.round(EdgeCordinates.second)-(int)(boxLength/2),Math.round(EdgeCordinates.first)+5,Math.round(EdgeCordinates.second)+(int)(boxLength/2));
            }
            root.addView(lineImage, params);
        }
    }
}
