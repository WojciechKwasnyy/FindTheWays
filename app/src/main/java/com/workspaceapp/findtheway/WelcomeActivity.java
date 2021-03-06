package com.workspaceapp.findtheway;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static java.lang.Thread.sleep;

public class WelcomeActivity extends AppCompatActivity {

    String TAG = "FindTheWays";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Typeface tf =  Typeface.createFromAsset(getAssets(), "FTW.ttf");
        Typeface tf2 =  Typeface.createFromAsset(getAssets(), "Sweet Sensations Personal Use.ttf");


        TextView textViewLoading = (TextView) findViewById(R.id.loading_textview);
        textViewLoading.setTypeface(tf2);
        TextView textViewTitle = (TextView) findViewById(R.id.title_textview);
        textViewTitle.setTypeface(tf2);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       // Log.i(TAG,user.getDisplayName());
        if(user!= null)
        {
            //Log.i(TAG,user.getDisplayName());
            gotomain.start();
        }
        else
        {
            gotologin.start();
        }




    }


    Thread gotologin = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                    startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                }
            });

        }
    });
    Thread gotomain = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                }
            });

        }
    });

}
