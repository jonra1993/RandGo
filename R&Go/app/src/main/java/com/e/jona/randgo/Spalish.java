package com.e.jona.randgo;

import android.content.Intent;
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

//https://www.youtube.com/watch?v=h_hTuaEpc-8

public class Spalish extends AppCompatActivity {

    private TextView tv;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalish);

        //tv=findViewById(R.id.tv);
        //iv=findViewById(R.id.iv);
        Animation myanim= AnimationUtils.loadAnimation(this,R.anim.mytransition);
        //tv.startAnimation(myanim);
        //iv.startAnimation(myanim);

        final Intent i = new Intent(this,MainActivity.class);
        Thread tim = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        tim.start();
    }

}
