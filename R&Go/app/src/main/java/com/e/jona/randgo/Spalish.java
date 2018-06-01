package com.e.jona.randgo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//https://www.youtube.com/watch?v=h_hTuaEpc-8

public class Spalish extends AppCompatActivity {

    RelativeLayout relat1;

    private TextView tvInicio;
    private ImageView iv;

    Handler handler= new Handler();
    Runnable runnable= new Runnable() {
        @Override
        public void run() {
            relat1.setVisibility(View.VISIBLE);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalish);

        //Visibilidad de barra de navegacion
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        relat1= (RelativeLayout) findViewById(R.id.relat1);

        tvInicio=findViewById(R.id.tvInicio);
        //iv=findViewById(R.id.iv);
        Animation myanim= AnimationUtils.loadAnimation(this,R.anim.mytransition);
        //iv.startAnimation(myanim);

        handler.postDelayed(runnable,2000);
        tvInicio.startAnimation(myanim);

        final Intent i = new Intent(this,MainActivity.class);
        Thread tim = new Thread(){
            public void run(){
                try{
                    sleep(5000);
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
