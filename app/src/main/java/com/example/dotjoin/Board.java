package com.example.dotjoin;

import android.util.Log;

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

    private int rows = 4, columns = 3;
    private float boxLength = 100;
    private float buffer = 5;
    private float gridTopLeftX = 200, gridTopLeftY = 200;
    private float gridMarginX = 50, gridMarginY = 50;
    private float firstNodeX, firstNodeY;
    private float gridFirstCol, gridLastCol, gridFirstRow, gridLastRow;
    private boolean horizontalEdge = false, verticalEdge = false;

//    float corX = 500, corY = 530;


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

    public void findEdgeCordinates(int EdgeNo){
        float xCorStart,yCorStart,xCorEnd,yCorEnd,midx,midy;
        if(EdgeNo<1 || EdgeNo>totalNoOfEdges()) return;

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
        midx = (xCorStart+xCorEnd)/2;
        midy = (yCorStart+yCorEnd)/2;
    }
}
