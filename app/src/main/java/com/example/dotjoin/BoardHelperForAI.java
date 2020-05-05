package com.example.dotjoin;

import android.util.Log;
import android.util.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class BoardHelperForAI{

    Board board;
    private int columns,totalEdges,totalBoxes;

    public BoardHelperForAI(Board board) {
        this.board = board;
        this.columns = board.getColumns();
        this.totalBoxes = board.getTotalBoxes();
        this.totalEdges = board.getTotalEdges();
    }

    public int giveNextEdgeNoEasy(){
        int edgeNo = findCompletingEdge();
        if(edgeNo!=-1)return edgeNo;
        Vector<Integer> safeEdges = findSafeEdges();
        if(safeEdges.size()==0) {
            for (int i = 1; i < this.totalEdges + 1; i++) {
                if (!this.board.getEdges().get(i)) return i;
            }
        }else {
            int index = (int)(safeEdges.size()*Math.random());
            return safeEdges.get(index);
        }
        return -1;
    }

    public int giveNextEdgeNoHard() throws CloneNotSupportedException{
        int completingEdgeNo = findCompletingEdge();
        Vector<Integer> safeEdges = findSafeEdges();
        int noOfSafeEdge = safeEdges.size();
        if(noOfSafeEdge>0){
            if(completingEdgeNo!=-1){
                return completingEdgeNo;
            }
            else{
                int index = (int)(safeEdges.size()*Math.random());
                return safeEdges.get(index);
            }
        }
        else{
            if(completingEdgeNo!=-1){
                Vector<Integer> completingEdges = findAllCompletingEdges();
                int noOfCompletingEdge = completingEdges.size();

                //if only one completing edge
                if(noOfCompletingEdge == 1){
                    Board b = (Board) this.board.clone();

                    int currentEdge = completingEdges.get(0);

                    //making copy of edges and boxes vector
                    Vector<Boolean> CopyEdges = (Vector)this.board.getEdges().clone();
                    Vector<Boolean> CopyBoxes = (Vector)this.board.getBoxes().clone();

                    int profit = TotalBoxesAStepCanMake(b,currentEdge);

                    //setting back vectors to original
                    this.board.setEdges(CopyEdges);
                    this.board.setBoxes(CopyBoxes);
                    b.setEdges(CopyEdges);
                    b.setBoxes(CopyBoxes);

                    if(profit>2) return currentEdge;
                    if(profit<2) return currentEdge;

                    ////making copy of edges and boxes vector
                    Vector<Boolean> CopyEdges2 = (Vector)this.board.getEdges().clone();
                    Vector<Boolean> CopyBoxes2 = (Vector)this.board.getBoxes().clone();

                    //calculating all possible loss
                    Vector<Pair<Integer,Integer> > AllLoss = new Vector<>();
                    for(int i=1;i<=b.getTotalEdges();i++){

                        if(!b.getEdges().get(i)){
                            Vector<Boolean> CopyEdges3 = (Vector)this.board.getEdges().clone();
                            Vector<Boolean> CopyBoxes3 = (Vector)this.board.getBoxes().clone();

                            Pair<Integer,Integer> p = new Pair<>(i,TotalBoxesAStepCanMake(b,i));

                            this.board.setEdges(CopyEdges3);
                            this.board.setBoxes(CopyBoxes3);
                            b.setEdges(CopyEdges3);
                            b.setBoxes(CopyBoxes3);

                            AllLoss.add(p);

                        }
                    }

                    this.board.setEdges(CopyEdges2);
                    this.board.setBoxes(CopyBoxes2);
                    b.setEdges(CopyEdges2);
                    b.setBoxes(CopyBoxes2);

                    //calculating min loss
                    int minLoss = 999999, minLossEdge = -1;
                    Vector<Integer> tempLosses = new Vector<>();
                    for(int i=0;i<AllLoss.size();i++){
                        tempLosses.add(AllLoss.get(i).second);
                        if(AllLoss.get(i).second<minLoss){
                            minLoss = AllLoss.get(i).second;
                            minLossEdge = AllLoss.get(i).first;
                        }
                    }

                    if(minLossEdge == -1)return currentEdge;

                    //if there are more than one pair of 2 boxes
                    if(AllLoss.size()>2){
                        Collections.sort(tempLosses);
                        //if min loss other than the pair is greater than 2
                        if(tempLosses.get(2)>2){
                            //special move
                            int currNodeNo = this.board.NodeNoGivenEdgeNo(currentEdge);
                            int currBoxNo = this.board.BoxNoGivenNodeNo(currNodeNo);
                            if(this.board.isEdgeNoHorizontal(currentEdge)){
                                if(currBoxNo!=-1){
                                    if(isThreeSidesMade(currBoxNo)==-1){
                                        // means condition is of form..reverse u(down opening u)
                                        //      *----*
                                        //      |    |
                                        //      *    *
                                        //      |    |
                                        //      *    *
                                        if(!this.board.getEdges().get(currentEdge+(2*this.board.getColumns())-1))
                                            return currentEdge+(2*this.board.getColumns())-1;
                                        if(!this.board.getEdges().get(currentEdge+this.board.getColumns()))
                                            return currentEdge+this.board.getColumns();
                                        if(!this.board.getEdges().get(currentEdge+this.board.getColumns()-1))
                                            return currentEdge+this.board.getColumns()-1;
                                    }
                                    else{
                                        // means condition is of form normal u, up oening u
                                        //      *    *
                                        //      |    |
                                        //      *    *
                                        //      |    |
                                        //      *----*
                                        if(!this.board.getEdges().get(currentEdge-(2*this.board.getColumns())+1))
                                            return currentEdge-(2*this.board.getColumns())+1;
                                        if(!this.board.getEdges().get(currentEdge-this.board.getColumns()+1))
                                            return currentEdge-this.board.getColumns()+1;
                                        if(!this.board.getEdges().get(currentEdge-this.board.getColumns()))
                                            return currentEdge-this.board.getColumns();
                                    }
                                }
                                else{
                                    Log.d(" cant reach here "," problem in idea.. vertical u");
                                }
                            }
                            else{
                                if(currBoxNo!=-1){
                                    if(isThreeSidesMade(currBoxNo)==-1){ //right opening u
                                        if(!this.board.getEdges().get(currentEdge+1))
                                            return currentEdge+1;
                                        if(!this.board.getEdges().get(currentEdge-this.board.getColumns()+1))
                                            return currentEdge-this.board.getColumns()+1;
                                        if(!this.board.getEdges().get(currentEdge+this.board.getColumns()))
                                            return currentEdge+this.board.getColumns();
                                    }else{ //left opening u
                                        if(!this.board.getEdges().get(currentEdge-1))
                                            return currentEdge-1;
                                        if(!this.board.getEdges().get(currentEdge-this.board.getColumns()))
                                            return currentEdge-this.board.getColumns();
                                        if(!this.board.getEdges().get(currentEdge+this.board.getColumns()-1))
                                            return currentEdge+this.board.getColumns()-1;
                                    }
                                }
                                else{
                                    Log.d(" cant reach here "," problem in idea.. horizontal u");
                                }
                            }
                        }
                    }

                    return currentEdge;


                }else{
                    //calculate all possible profits
                    //first go with profit 2 but common edge (single completing edge)
                    //than go with profit 1
                    //than go with profit in order max to min ... means return edge no with max profit

                    Board b = (Board) this.board.clone();
                    Vector<Pair<Integer,Integer> > profits = new Vector<>();

                    Collections.sort(completingEdges);
                    Vector<Boolean> CopyEdges = (Vector)this.board.getEdges().clone();
                    Vector<Boolean> CopyBoxes = (Vector)this.board.getBoxes().clone();

                    //returning a edge which is common.. producing profit of 2
                    for(int i=0;i<completingEdges.size();i++){
                        if(i>0){
                            if(completingEdges.get(i).equals(completingEdges.get(i-1))){
                                return completingEdges.get(i);
                            }
                        }
                        Vector<Boolean> CopyEdges2 = (Vector)this.board.getEdges().clone();
                        Vector<Boolean> CopyBoxes2 = (Vector)this.board.getBoxes().clone();

                        Pair<Integer,Integer> p = new Pair<>(completingEdges.get(i),TotalBoxesAStepCanMake(b,completingEdges.get(i)));

                        this.board.setEdges(CopyEdges2);
                        this.board.setBoxes(CopyBoxes2);
                        b.setEdges(CopyEdges2);
                        b.setBoxes(CopyBoxes2);

                        profits.add(p);
                    }

                    this.board.setEdges(CopyEdges);
                    this.board.setBoxes(CopyBoxes);
                    b.setEdges(CopyEdges);
                    b.setBoxes(CopyBoxes);

                    //return edge with profit of 1
                    for(int i=0;i<profits.size();i++){
                        if(profits.get(i).second==1){
                            return profits.get(i).first;
                        }
                    }

                    //returning edge with max profit
                    int maxProfit = -1,maxProfitEdge = -1;
                    for(int i=0;i<profits.size();i++){
                        if(profits.get(i).second>maxProfit){
                            maxProfit = profits.get(i).second;
                            maxProfitEdge = profits.get(i).first;
                        }
                    }
                    return maxProfitEdge;

                }
            } else{
                //calculate all possible loss and go for minimum loss.
                Board b = (Board) this.board.clone();

                Vector<Pair<Integer,Integer> > AllLoss = new Vector<>();

                //calculating all possible loss
                Vector<Boolean> CopyEdges = (Vector)this.board.getEdges().clone();
                Vector<Boolean> CopyBoxes = (Vector)this.board.getBoxes().clone();

                for(int i=1;i<=b.getTotalEdges();i++){
                    if(!b.getEdges().get(i)){
                        Vector<Boolean> CopyEdges2 = (Vector)this.board.getEdges().clone();
                        Vector<Boolean> CopyBoxes2 = (Vector)this.board.getBoxes().clone();

                        Pair<Integer,Integer> p = new Pair<>(i,TotalBoxesAStepCanMake(b,i));

                        this.board.setEdges(CopyEdges2);
                        this.board.setBoxes(CopyBoxes2);
                        b.setEdges(CopyEdges2);
                        b.setBoxes(CopyBoxes2);

                        AllLoss.add(p);
                    }
                }

                this.board.setEdges(CopyEdges);
                this.board.setBoxes(CopyBoxes);
                b.setEdges(CopyEdges);
                b.setBoxes(CopyBoxes);

                //calculating min loss
                int minLoss = 999999, minLossEdge = -1;
                for(int i=0;i<AllLoss.size();i++){
                    if(AllLoss.get(i).second<minLoss){
                        minLoss = AllLoss.get(i).second;
                        minLossEdge = AllLoss.get(i).first;
                    }
                }
                return minLossEdge;

            }
        }
    }

    public int calculateLeftBoxProfit(Board b,int EdgeNo){
        if(EdgeNo<1 || EdgeNo>b.getTotalEdges()) return 0;
        int profit = b.isBoxCompleted(EdgeNo).size();
        Vector<Boolean> boardEdges = b.getEdges();
        if(boardEdges.get(EdgeNo))profit = 0;
        else b.makeMoveAt(EdgeNo);
        if(!b.ifLeftBoxExist(EdgeNo))return profit;
        int count = 0,temp = -1,boxMode = -1;
        if(boardEdges.get(EdgeNo-1))count++; else {temp = (EdgeNo-1); boxMode = 1;} //left
        if(boardEdges.get(EdgeNo-b.getColumns()))count++; else {temp = (EdgeNo-b.getColumns()); boxMode = 2;} //top
        if(boardEdges.get(EdgeNo+b.getColumns()-1))count++; else {temp = (EdgeNo+b.getColumns()-1); boxMode = 3;} //bottom
        if(count==2){
            if(boxMode == 1){ return profit+calculateLeftBoxProfit(b,temp);}
            if(boxMode == 2){ return profit+calculateTopBoxProfit(b,temp);}
            if(boxMode == 3){return profit+calculateBottomBoxProfit(b,temp);}
        }
        return profit;
    }

    public int calculateRightBoxProfit(Board b,int EdgeNo){
        if(EdgeNo<1 || EdgeNo>b.getTotalEdges()) return 0;
        int profit = b.isBoxCompleted(EdgeNo).size();
        Vector<Boolean> boardEdges = b.getEdges();
        if(boardEdges.get(EdgeNo))profit = 0;
        else b.makeMoveAt(EdgeNo);

        if(!b.ifRightBoxExist(EdgeNo))return profit;
        int count = 0,temp = -1,boxMode = -1;

        if(boardEdges.get(EdgeNo+1))count++; else {temp = (EdgeNo+1); boxMode = 1;} //right
        if(boardEdges.get(EdgeNo-b.getColumns()+1))count++; else {temp = (EdgeNo-b.getColumns()+1); boxMode = 2;} //top
        if(boardEdges.get(EdgeNo+b.getColumns()))count++; else {temp = (EdgeNo+b.getColumns()); boxMode = 3;} //bottom
        if(count==2){
            if(boxMode == 1){return profit+calculateRightBoxProfit(b,temp);}
            if(boxMode == 2){return profit+calculateTopBoxProfit(b,temp);}
            if(boxMode == 3){ return profit+calculateBottomBoxProfit(b,temp);}
        }
        return profit;
    }

    public int calculateBottomBoxProfit(Board b,int EdgeNo){
        if(EdgeNo<1 || EdgeNo>b.getTotalEdges()) return 0;
        int profit = b.isBoxCompleted(EdgeNo).size();
        Vector<Boolean> boardEdges = b.getEdges();
        if(boardEdges.get(EdgeNo))profit = 0;
        else b.makeMoveAt(EdgeNo);

        if(!b.ifBottomBoxExist(EdgeNo))return profit;
        int count = 0,temp = -1,boxMode = -1;

        if(boardEdges.get(EdgeNo+b.getColumns()-1))count++; else {temp = (EdgeNo+b.getColumns()-1); boxMode = 1;} //left
        if(boardEdges.get(EdgeNo+b.getColumns()))count++; else {temp = (EdgeNo+b.getColumns()); boxMode = 2;} //right
        if(boardEdges.get(EdgeNo+(2*b.getColumns())-1))count++; else {temp = (EdgeNo+(2*b.getColumns())-1); boxMode = 3;} //bottom
        if(count==2){
            if(boxMode == 1){return profit+calculateLeftBoxProfit(b,temp);}
            if(boxMode == 2){return profit+calculateRightBoxProfit(b,temp);}
            if(boxMode == 3){return profit+calculateBottomBoxProfit(b,temp);}
        }
        return profit;
    }

    public int calculateTopBoxProfit(Board b,int EdgeNo){
        if(EdgeNo<1 || EdgeNo>b.getTotalEdges()) return 0;
        int profit = b.isBoxCompleted(EdgeNo).size();
        Vector<Boolean> boardEdges = b.getEdges();
        if(boardEdges.get(EdgeNo))profit = 0;
        else b.makeMoveAt(EdgeNo);

        if(!b.ifTopBoxExist(EdgeNo))return profit;
        int count = 0,temp = -1,boxMode = -1;

        if(boardEdges.get(EdgeNo-b.getColumns()))count++; else {temp = (EdgeNo-b.getColumns()); boxMode = 1;} //left
        if(boardEdges.get(EdgeNo-b.getColumns()+1))count++; else {temp = (EdgeNo-b.getColumns()+1); boxMode = 2;} //right
        if(boardEdges.get(EdgeNo-(2*b.getColumns())+1))count++; else {temp = (EdgeNo-(2*b.getColumns())+1); boxMode = 3;} //top
        if(count==2){
            if(boxMode == 1){return profit+calculateLeftBoxProfit(b,temp);}
            if(boxMode == 2){return profit+calculateRightBoxProfit(b,temp);}
            if(boxMode == 3) {return profit+calculateTopBoxProfit(b,temp);}
        }
        return profit;
    }

    public int TotalBoxesAStepCanMake(Board b,int EdgeNo){
        if(EdgeNo<1 || EdgeNo>b.getTotalEdges())return 0;
        if(b.isEdgeNoHorizontal(EdgeNo)){
            int top = calculateTopBoxProfit(b,EdgeNo);
            int bottom = calculateBottomBoxProfit(b,EdgeNo);
            return top+bottom;
        }else {
            int left = calculateLeftBoxProfit(b,EdgeNo);
            int right = calculateRightBoxProfit(b,EdgeNo);
            return left+right;
        }
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
            if(this.board.getEdges().get(boxEdges.get(i))){count++;}
            else ans = boxEdges.get(i);
        }
        if(count==3) return ans;
        else return -1;
    }

    public int findCompletingEdge(){
        for(int i=1;i<this.totalBoxes+1;i++){
            if(!this.board.getBoxes().get(i)){
                int edgeNo = isThreeSidesMade(i);
                if(edgeNo!=-1) return edgeNo;
            }
        }
        return -1;
    }

    public Vector<Integer> findAllCompletingEdges(){
        Vector<Integer> completingEdges = new Vector<>();
        for(int i=1;i<this.totalBoxes+1;i++){
            if(!this.board.getBoxes().get(i)){
                int edgeNo = isThreeSidesMade(i);
                if(edgeNo!=-1) completingEdges.add(edgeNo);
            }
        }
        return completingEdges;
    }

    //edge is called safe, if by drawing it, it does not set any box's 3rd edge.
    public Vector<Integer> findSafeEdges(){
        Vector<Integer> safeEdges = new Vector<>();
        for(int i=1;i<this.totalEdges+1;i++){
            if(!this.board.getEdges().get(i)){
                boolean flag1 = true, flag2 = true;
                if(this.board.isEdgeNoHorizontal(i)){
                    if(this.board.ifTopBoxExist(i)){
                        int count = 0;
                        if(this.board.getEdges().get(i-this.columns))count++;
                        if(this.board.getEdges().get(i-this.columns+1))count++;
                        if(this.board.getEdges().get(i-(2*this.columns)+1))count++;
                        if(count>1)flag1 = false;
                    }
                    if(this.board.ifBottomBoxExist(i)){
                        int count = 0;
                        if(this.board.getEdges().get(i+this.columns-1))count++;
                        if(this.board.getEdges().get(i+this.columns))count++;
                        if(this.board.getEdges().get(i+(2*this.columns)-1))count++;
                        if(count>1)flag2 = false;
                    }
                }
                else{
                    if(this.board.ifLeftBoxExist(i)){
                        int count = 0;
                        if(this.board.getEdges().get(i-1))count++;
                        if(this.board.getEdges().get(i-this.columns))count++;
                        if(this.board.getEdges().get(i+this.columns-1))count++;
                        if(count>1)flag1 = false;
                    }
                    if(this.board.ifRightBoxExist(i)){
                        int count = 0;
                        if(this.board.getEdges().get(i+1))count++;
                        if(this.board.getEdges().get(i-this.columns+1))count++;
                        if(this.board.getEdges().get(i+this.columns))count++;
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
