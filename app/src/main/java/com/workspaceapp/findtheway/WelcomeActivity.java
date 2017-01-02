package com.workspaceapp.findtheway;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

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

    }

}
