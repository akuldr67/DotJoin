package com.example.dotjoin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    //Firebase
    private DatabaseReference mDatabaseRef;

    //Layout
    private Button singlePlayer;
    private Button multiPlayerOffline;
    private Button multiPlayerOnline;

    //Shared Prefrences
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor myEdit;

    String mCurrentUserName;
//    private ImageButton chatButton;

    private AdView bannerAdView;

    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        //**** ad code starts ****
        //initializing mobile ad sdk
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //****** showing banner ad start *******
        //Loading banner ad in test device
//        List<String> testDeviceIds = Arrays.asList("315EA26B97DB5CBDE5501CB99E69E32A");
        bannerAdView = findViewById(R.id.bannerAdMainActivity);

//        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//        MobileAds.setRequestConfiguration(configuration);

        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.loadAd(adRequest);
        //**** showing banner ad end ******


        //**** showing interstitial ad start *****
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        //*** showing interstitial ad end *****





        mDatabaseRef= FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.child("Rooms");

        //finding Layouts
        singlePlayer = findViewById(R.id.main_button_single_player);
        multiPlayerOffline=findViewById(R.id.main_button_multi_offline);
        multiPlayerOnline=findViewById(R.id.main_button_multi_online);

        //Creating Intent for each button in respective onClick listeners

        //singlePlayer
        singlePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SinglePlayerDialog.class);
                startActivity(intent);

//                mInterstitialAd.loadAd(new AdRequest.Builder().build());

//                if(mInterstitialAd.isLoaded()){
//                    mInterstitialAd.show();
//                }
//                else{
//                    Log.d("check"," interstitial ad wasn't loaded");
//                    Intent intent=new Intent(MainActivity.this,SinglePlayerDialog.class);
//                    startActivity(intent);
//                }
//                interstitialAdEvents();

            }
        });

        //multiPlayerOffline
        multiPlayerOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MultiPlayerOfflineDialog.class);
                startActivity(intent);
            }
        });

        //multiPlayerOnline
        multiPlayerOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,MultiPlayerOnline.class);
                startActivity(intent);
            }
        });

        mSharedPreferences = getSharedPreferences("com.example.dotjoin.file",Context.MODE_PRIVATE);

        mCurrentUserName=mSharedPreferences.getString("UserName","");
        if(mCurrentUserName.equals("")){

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView=inflater.inflate(R.layout.name_dialog,null);
            builder.setView(dialogView);
            final EditText input = dialogView.findViewById(R.id.name_input);
            final Button submit = dialogView.findViewById(R.id.name_submit_button);

            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(input.getText().toString().equals("")){
                                Toast.makeText(getApplicationContext(),"Please Enter Your Name",Toast.LENGTH_LONG).show();
                            }
                            else if(input.getText().toString().length()>8){
                                Toast.makeText(getApplicationContext(),"Maximum 8 characters are allowed",Toast.LENGTH_LONG).show();
                            }
                            else{
                                myEdit=mSharedPreferences.edit();
                                mCurrentUserName=input.getText().toString();
                                myEdit.putString("UserName",mCurrentUserName);
                                myEdit.apply();
                                dialog.dismiss();
                            }
                        }
                    });
                }
            });
            alertDialog.show();
        }
//        //Storing Player No in Shared Preferences
//        int playerNo=mSharedPreferences.getInt("playerNo",-2);
//        if(playerNo==-2){
//            myEdit=mSharedPreferences.edit();
//            playerNo=-1;
//            myEdit.putInt("playerNo",playerNo);
//            myEdit.apply();
//        }

    }


    public void showPopupMenu(View view){
        PopupMenu popUp = new PopupMenu(this, view);
        popUp.setOnMenuItemClickListener(this);
        popUp.inflate(R.menu.popup_menu);
        popUp.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.changeName:
                Intent changeNameIntent=new Intent(MainActivity.this,ChangeName.class);
                startActivity(changeNameIntent);
                return true;

            case R.id.AboutGame:
                Intent abouGameIntent=new Intent(MainActivity.this,aboutGame.class);
                startActivity(abouGameIntent);
                return true;

            case R.id.AboutUs:
                Intent aboutUsIntent=new Intent(MainActivity.this,aboutUs.class);
                startActivity(aboutUsIntent);
                return true;

            default:
                return false;
        }
    }


//    private void interstitialAdEvents(){
//        //listener for single player start interstitial ad
//        mInterstitialAd.setAdListener(new AdListener(){
//
//            @Override
//            public void onAdClosed() {
//                Intent intent=new Intent(MainActivity.this,SinglePlayerDialog.class);
//                startActivity(intent);
//            }
//
//        });
//    }


    @Override
    public void onBackPressed() {
        Intent exitGameIntent = new Intent(MainActivity.this, ExitGameDialog.class);
        startActivity(exitGameIntent);
    }
}
