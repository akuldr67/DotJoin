package com.example.dotjoin;

import android.content.Context;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

//*************
//*Assumptions*
//*************

//Original Image Dimensions

//SingleLine
//Width - 15dp
//Height- 100dp

//Buffer for 3*3 (size=4) is 60

public class LayoutUtils {

    private static float lineWidth=15,lineHeight=100,threeBuffer=50;


    public LayoutUtils() {
    }

    public void drawBoard(int rows, int columns, Context context, ConstraintLayout root, float width,float topLeftX, float topLeftY){
        int i,j;
        for(i=0;i<rows;i++){
            for(j=0;j<columns;j++){
                ImageView dotImage = new ImageView(context);
                dotImage.setTranslationZ(3f);
                dotImage.setImageResource(R.drawable.dot);
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams((int)getDiaOfDot(width,columns),(int)getDiaOfDot(width,columns));
                dotImage.setX(topLeftX+getXCoordinate(width,columns,j));
                dotImage.setY(topLeftY+getYCoordinate(width,columns,i));
                root.addView(dotImage,params);
            }
        }
    }

    public float getBoxLength(float width, int columns){
        return ((width*lineHeight)/((lineHeight*(columns-1))-(lineWidth*(columns-2))));
    }

    public float getHeightofGrid(float width,int rows,int columns){
        return (width * (rows-1))/(columns-1);
    }

    public float getDiaOfDot(float width,int columns){
        return (lineWidth*width)/((lineHeight*(columns-1))-(lineWidth*(columns-2)));
    }

    public float getXCoordinate(float width, int columns, int columnNo){
        return columnNo*(getBoxLength(width,columns)-getDiaOfDot(width,columns));
    }

    public float getYCoordinate(float width, int columns,int rowNo){
        return rowNo*(getBoxLength(width,columns)-getDiaOfDot(width,columns));
    }

    public float getBuffer(int columns){
        return((float) (Math.sqrt(3)*threeBuffer)/(float) Math.sqrt(columns-1));
    }
}
