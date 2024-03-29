package com.arpitakuldr.dotjoin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Vector;

public class SinglePlayer extends AppCompatActivity {

    private ImageView boardImage;
    public float imageHeight,imageWidth;
    private ConstraintLayout rootLayout;
    private Board board;
    private Game game;
    private View view;
    private float posX,posY;
    private int difficultyLevel,boardSize,flag;
    private LayoutUtils layoutUtils;
    private Vector<TextView> scoreViewVector;
    private CountDownTimer timer;
    private MutableLiveData<Integer>lastEdgeUpdated;
    private Vector<Integer>highlightedBoxes,unhighlightedBoxes;

    private AdView bannerAdView;

    private int bannerHeight = dpToPx(50);

    public static Activity AcSinglePlayer;

    public static View rootViewShare;

    private InterstitialAd mSinglePlayerInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws RuntimeException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        AcSinglePlayer = this;
        rootViewShare = getWindow().getDecorView().findViewById(R.id.constraint);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
//        List<String> testDeviceIds = Arrays.asList("315EA26B97DB5CBDE5501CB99E69E32A");
        bannerAdView = findViewById(R.id.bannerAdSinglePlayer);

//        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//        MobileAds.setRequestConfiguration(configuration);

        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.loadAd(adRequest);


        mSinglePlayerInterstitialAd = new InterstitialAd(this);
        mSinglePlayerInterstitialAd.setAdUnitId(getString(R.string.interstitialAdSinglePlayerId));
        mSinglePlayerInterstitialAd.loadAd(new AdRequest.Builder().build());


        Intent intent = getIntent();
        difficultyLevel=intent.getIntExtra("difficulty",-1);
        if(difficultyLevel==-1){
            Log.d("checkk","Did not get difficulty");
        }

        boardSize=intent.getIntExtra("size",-1);
        if(boardSize==-1){
            Log.d("checkk","Did not get size");
        }

        lastEdgeUpdated = new MutableLiveData<>();
        lastEdgeUpdated.setValue(-1);

        //Finding Layouts
        boardImage=findViewById(R.id.boardImage);
        rootLayout=findViewById(R.id.constraint);
        view =findViewById(R.id.view);

        highlightedBoxes=new Vector<>();
        unhighlightedBoxes=new Vector<>();

        highlightedBoxes.add(R.drawable.highlighted_color_box_blue);
        highlightedBoxes.add(R.drawable.highlighted_color_box_red);

        unhighlightedBoxes.add(R.drawable.unhighlighted_color_box_blue);
        unhighlightedBoxes.add(R.drawable.unhighlighted_color_box_red);


        //Getting ViewTreeObserver of the board Image
        ViewTreeObserver vto = boardImage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                boardImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                layoutUtils=new LayoutUtils();

//                //Dialog Box that accepts no. of players
//                String[] options = new String[]{"Easy","Hard"};
//
//                final AlertDialog.Builder noOfUsersDialog = new AlertDialog.Builder(SinglePlayer.this);
//                noOfUsersDialog.setTitle("Select difficulty level");
//                noOfUsersDialog.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == 0) difficultyLevel = 1;
//                        else if (which == 1) difficultyLevel = 2;
//
//                        //Dialog Box that accepts board size
//                        CharSequence[] sizeOptions = new CharSequence[]{"3*3 ", "4*4", "5*5", "6*6", "7*7"};
//
//                        AlertDialog.Builder sizeDialog = new AlertDialog.Builder(SinglePlayer.this);
//                        sizeDialog.setTitle("Size");
//                        sizeDialog.setItems(sizeOptions, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (which == 0) boardSize = 4;
//                                else if (which == 1) boardSize = 5;
//                                else if (which == 2) boardSize = 6;
//                                else if (which == 3) boardSize = 7;
//                                else if (which == 4) boardSize = 8;

                                scoreViewVector=new Vector<TextView>();
                                scoreViewVector.setSize(2);

                                //Getting width and height of the image view
                                imageHeight = boardImage.getHeight();
                                imageWidth = boardImage.getWidth();

                                //Initializing Board, Game, and layoutUtils
                                layoutUtils.drawBoard(boardSize, boardSize, SinglePlayer.this, rootLayout, imageWidth, 100, 100 + ((imageHeight - imageWidth - bannerHeight) / 2),0);
                                board = new Board(boardSize, boardSize, 100, 100 + ((imageHeight - imageWidth - bannerHeight) / 2), imageWidth);
                                game = new Game(0, 2,  board,new ArrayList<Player>());

                                game.players.add(new Player("You",R.drawable.colour_box_blue,0,0,"",0,1,0));
                                game.players.add(new Player("Computer",R.drawable.colour_box_red,0,1,"",0,1,0));

                                //Setting params for corner text Views that display score
                                scoreViewVector.set(0,(TextView) findViewById(R.id.player1));
                                scoreViewVector.elementAt(0).setText(game.players.get(0).getName()+" - "+game.players.get(0).getScore());
                                scoreViewVector.elementAt(0).setBackgroundResource(unhighlightedBoxes.get(0));
                                scoreViewVector.set(1,(TextView) findViewById(R.id.computer_player));
                                scoreViewVector.elementAt(1).setText(game.players.get(1).getName()+" - "+game.players.get(1).getScore());
                                scoreViewVector.elementAt(1).setBackgroundResource(unhighlightedBoxes.get(1));


                                //Highlighting the first Player TextView
                                scoreViewVector.elementAt(0).setBackgroundResource(highlightedBoxes.get(0));
                                scoreViewVector.elementAt(0).setTypeface(Typeface.DEFAULT_BOLD);
                                scoreViewVector.elementAt(0).setTextColor(ContextCompat.getColor(SinglePlayer.this,R.color.black));

                                lastEdgeUpdated.observe(SinglePlayer.this, new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        Log.d("checkk","onChange Triggered");
                                        if(game.getCurrentPlayer()==0){
                                            layoutUtils.drawBoard(boardSize, boardSize, SinglePlayer.this, rootLayout, imageWidth, 100, 100 + ((imageHeight - imageWidth - bannerHeight) / 2),0);
                                            view.setOnTouchListener(new View.OnTouchListener() {
                                                @Override
                                                public boolean onTouch(View v, MotionEvent event) {
                                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                                        //Getting Coordinates of Touch
                                                        posX = event.getX();
                                                        posY = event.getY();
                                                        //Getting Edge No touched
                                                        int edgeNo = game.board.EdgeNoGivenCor(posX, posY);
                                                        ArrayList<Boolean> edges = board.getEdges();
                                                        if (edgeNo != -1 && !edges.get(edgeNo)) {
                                                            executeTurn(edgeNo);
                                                        }
                                                    }
                                                    return true;
                                                }
                                            });
                                        }
                                        else if(game.getCurrentPlayer()==1){
                                            layoutUtils.drawBoard(boardSize, boardSize, SinglePlayer.this, rootLayout, imageWidth, 100, 100 + ((imageHeight - imageWidth - bannerHeight) / 2),1);
                                            view.setEnabled(false);
                                            timer = new CountDownTimer(500,100) {
                                                @Override
                                                public void onTick(long millisUntilFinished) {

                                                }

                                                @Override
                                                public void onFinish() {
                                                    Log.d("checkk", "Timer Finished, Starting computers turn");
                                                    BoardHelperForAI b = new BoardHelperForAI(game.board);
                                                    int computerEdgeNo;
                                                    view.setEnabled(true);
                                                    if (difficultyLevel == 1)
                                                        computerEdgeNo = b.giveNextEdgeNoEasy();
                                                    else {
                                                        try {
                                                            computerEdgeNo = b.giveNextEdgeNoHard();
                                                        } catch (CloneNotSupportedException ex) {
                                                            throw new RuntimeException(ex);
                                                        }
                                                    }
                                                    Log.d("checkk", "Starting Execution of computers turn");
                                                    executeTurn(computerEdgeNo);
                                                }
                                            }.start();
                                        }
                                    }
                                });
                                lastEdgeUpdated.setValue(-2);

////                                                Log.d("checkk","Loop starting");
////                                                MutableLiveData<Integer>lastEdgeUpdated=new MutableLiveData<>();
////                                                lastEdgeUpdated.setValue(-1);
////                                                lastEdgeUpdated.observe(SinglePlayer.this, new Observer<Integer>() {
////                                                    @Override
////                                                    public void onChanged(Integer integer) {
////                                                        if(game.getCurrentPlayer()==1){
////
////                                                    }
////                                                });
////                                                    lastEdgeUpdated=game.getLastEdgeUpdated();
////                                                        timer = new CountDownTimer(5000, 1000) {
////                                                            @Override
////                                                            public void onTick(long millisUntilFinished) {
////                                                                Log.d("checkk","Ticking");
////                                                            }
////
////                                                            @Override
////                                                            public void onFinish() {
////                                                                Log.d("checkk", "Timer Finished, Starting computers turn");
////                                                                BoardHelperForAI b = new BoardHelperForAI(game.board);
////                                                                int computerEdgeNo;
////                                                                if (difficultyLevel == 1)
////                                                                    computerEdgeNo = b.giveNextEdgeNoEasy();
////                                                                else {
////                                                                    try {
////                                                                        computerEdgeNo = b.giveNextEdgeNoHard();
////                                                                    } catch (CloneNotSupportedException ex) {
////                                                                        throw new RuntimeException(ex);
////                                                                    }
////                                                                }
////                                                                Log.d("checkk", "Starting Execution of computers turn");
////                                                                executeTurn(computerEdgeNo);
////                                                            }
////                                                        };
////                                                        Log.d("checkk","starting the timer");
////                                                        timer.start();
////                                                    }
////                                                }
////                                            }
////                                        }
////                                        return true;
////                                    }
////                                });
//                            }
//                        });
//                        AlertDialog alertDialog=sizeDialog.create();
//                        alertDialog.setCanceledOnTouchOutside(false);
//                        alertDialog.show();
//                    }
//                });
//                AlertDialog alertDialog = noOfUsersDialog.create();
//                alertDialog.setCanceledOnTouchOutside(false);
//                alertDialog.show();
            }
        });
    }

    private void executeTurn(int edgeNo){
        Log.d("checkk","got edgeNo"+edgeNo);
        game.setLastEdgeUpdated(edgeNo);
        game.board.makeMoveAt(edgeNo);
        Log.d("checkk","Placing Edge");
        game.board.placeEdgeGivenEdgeNo(game.lastEdgeUpdated, getApplicationContext(), rootLayout);

        int NoOfNewBox = game.board.isBoxCompleted(game.lastEdgeUpdated).size();
        if (NoOfNewBox == 0) {
            Log.d("checkk","no boxes made, Changing the turn");
            scoreViewVector.elementAt(game.getCurrentPlayer()).setBackgroundResource(unhighlightedBoxes.get(game.getCurrentPlayer()));
            scoreViewVector.elementAt(game.getCurrentPlayer()).setTypeface(Typeface.DEFAULT);
            scoreViewVector.elementAt(game.getCurrentPlayer()).setTextColor(ContextCompat.getColor(SinglePlayer.this,R.color.grey));

            game.nextTurn();

            scoreViewVector.elementAt(game.getCurrentPlayer()).setBackgroundResource(highlightedBoxes.get(game.getCurrentPlayer()));
            scoreViewVector.elementAt(game.getCurrentPlayer()).setTypeface(Typeface.DEFAULT_BOLD);
            scoreViewVector.elementAt(game.getCurrentPlayer()).setTextColor(ContextCompat.getColor(SinglePlayer.this,R.color.black));
        } else {
            Log.d("checkk","boxes made, same players turn");
            ArrayList<Integer> newBoxNodes = game.board.isBoxCompleted(game.lastEdgeUpdated);
            for (int i = 0; i < NoOfNewBox; i++) {
                game.colourBox(board,newBoxNodes.get(i), getApplicationContext(), rootLayout);
            }
            game.increaseScore();
        }
        scoreViewVector.elementAt(game.getCurrentPlayer()).setText(game.players.get(game.getCurrentPlayer()).getName()+" - "+game.players.get(game.getCurrentPlayer()).getScore());
        lastEdgeUpdated.setValue(edgeNo);
        Log.d("checkk","Checking if game is completed or not");
        if(game.gameCompleted()){
            endGame();
        }
    }



    protected void endGame(){
        timer.cancel();
        String result = "";

        //sorting score
        Vector <Integer> sortOrderScore = new Vector<>();
        if(game.players.get(0).getScore()<game.players.get(1).getScore()){
            sortOrderScore.add(1);
            sortOrderScore.add(0);
        } else {
            sortOrderScore.add(0);
            sortOrderScore.add(1);
        }

        for (int i = 0; i < 2; i++) {
            if (i == 1) {
                result = result + game.players.get(sortOrderScore.get(i)).getName() + " - " + game.players.get(sortOrderScore.get(i)).getScore();
            } else {
                result = result + game.players.get(sortOrderScore.get(i)).getName() + " - " + game.players.get(sortOrderScore.get(i)).getScore() + "\n";
            }
        }

        Intent intent = new Intent(SinglePlayer.this,SinglePlayerEndGame.class);
        intent.putExtra("Heading",game.resultString());
        intent.putExtra("Result",result);
        intent.putExtra("Activity","Single");
        intent.putExtra("difficulty",difficultyLevel);
        intent.putExtra("size",boardSize);
//        startActivity(intent);


        if(mSinglePlayerInterstitialAd.isLoaded()){
            mSinglePlayerInterstitialAd.show();
        }
        else{
            Log.d("check"," Single Player EndGame interstitial ad wasn't loaded");
            startActivity(intent);
        }
        interstitialAdEvents(intent);





//        finish();
        //Showing Final Dialog Box
//        AlertDialog.Builder builder = new AlertDialog.Builder(SinglePlayer.this);
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            builder = new AlertDialog.Builder(SinglePlayer.this, android.R.style.Theme_Material_Light_Dialog_Alert);
////        } else {
////            builder = new AlertDialog.Builder(SinglePlayer.this);
////        }
//        builder.setTitle(game.resultString());
//
//        //Showing final ScoreBoard
//        TextView textView = new TextView(getApplicationContext());
//        textView.setPadding(60, 50, 20, 40);
//        textView.setLineSpacing(1.5f, 1.5f);
//        textView.setTextSize(16);
//        String result = "";
//        for (int i = 0; i < 2; i++) {
//            if (i == 1) {
//                result = result + game.players.get(i).getName() + " - " + game.players.get(i).getScore();
//            } else {
//                result = result + game.players.get(i).getName() + " - " + game.players.get(i).getScore() + "\n";
//            }
//        }
//        textView.setText(result);
//        builder.setView(textView);
//
//        //Replay Button
//        builder.setPositiveButton("Replay", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(SinglePlayer.this, SinglePlayerDialog.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        //Home Button
//        builder.setNegativeButton("Home", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(SinglePlayer.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//        AlertDialog alertDialog=builder.create();
//        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.show();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    private void interstitialAdEvents(final Intent EndGameIntent){
        //listener for interstitial ad
        mSinglePlayerInterstitialAd.setAdListener(new AdListener(){

            @Override
            public void onAdClosed() {
                startActivity(EndGameIntent);
            }

        });
    }
}
