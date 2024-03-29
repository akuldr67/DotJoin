package com.arpitakuldr.dotjoin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    //Firebase
    private DatabaseReference mDatabaseRef;

    //Layout
//    private ImageView singlePlayer;
//    private ImageView multiPlayerOffline;
//    private ImageView multiPlayerOnline;

    //Shared Preferences
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor myEdit;

    String mCurrentUserName;
//    private ImageButton chatButton;

    private AdView bannerAdView;

    private InterstitialAd mOnlineEndInterstitialAd;
    private String adCheckVariable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
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
        mOnlineEndInterstitialAd = new InterstitialAd(this);
        mOnlineEndInterstitialAd.setAdUnitId(getString(R.string.interstitialAdMultiPlayerOnlineId));
        mOnlineEndInterstitialAd.loadAd(new AdRequest.Builder().build());
        //*** showing interstitial ad end *****




        mDatabaseRef= FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.child("Rooms");



        mSharedPreferences = getSharedPreferences("com.arpitakuldr.dotjoin.file",Context.MODE_PRIVATE);

        mCurrentUserName=mSharedPreferences.getString("UserName","");
        if(mCurrentUserName.equals("")){

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView=inflater.inflate(R.layout.name_dialog,null);
            builder.setView(dialogView);
            final AppCompatEditText input = dialogView.findViewById(R.id.name_input);
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
            alertDialog.setCancelable(false);
            alertDialog.show();
        }

        adCheckVariable=mSharedPreferences.getString("adCheckVariable","");
        if(adCheckVariable.equals("")){
            myEdit=mSharedPreferences.edit();
            myEdit.putString("adCheckVariable","1");
            myEdit.apply();
        }

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

            case R.id.ShareApp:
                final String appPackageName = getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this new dots and boxes game: https://play.google.com/store/apps/details?id=" + appPackageName);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;

            case R.id.RateUs:
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
                }
                catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
                }
                return true;

            default:
                return false;
        }
    }


    public void onClickSinglePlayer(View view){
        Intent intent=new Intent(MainActivity.this,SinglePlayerDialog.class);
        startActivity(intent);
    }

    public void onClickMultiPlayerOffline(View view){
        Intent intent=new Intent(MainActivity.this,MultiPlayerOfflineDialog.class);
        startActivity(intent);
    }

    public void onClickMultiPlayerOnline(View view) {
        Intent intent= new Intent(MainActivity.this,MultiPlayerOnline.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        Intent exitGameIntent = new Intent(MainActivity.this, ExitGameDialog.class);
        startActivity(exitGameIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adCheckVariable=mSharedPreferences.getString("adCheckVariable","");
        if(adCheckVariable.equals("2")){
            if(mOnlineEndInterstitialAd.isLoaded()){
                mOnlineEndInterstitialAd.show();
                interstitialAdEvents();
            }
            myEdit=mSharedPreferences.edit();
            myEdit.putString("adCheckVariable","1");
            myEdit.apply();
        }

    }


    private void interstitialAdEvents(){
        //listener for interstitial ad
        mOnlineEndInterstitialAd.setAdListener(new AdListener(){

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mOnlineEndInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

    }

}
