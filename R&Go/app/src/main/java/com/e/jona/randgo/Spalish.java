package com.e.jona.randgo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
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
import android.widget.Toast;

import java.util.Locale;

//https://www.youtube.com/watch?v=h_hTuaEpc-8

public class Spalish extends AppCompatActivity {

    RelativeLayout relat1, relatline;

    TextToSpeech toSpeech;
    int resultt;

    Animation myanim;

    private TextView tvInicio;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalish);

        //Visibilidad de barra de navegacion
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        relat1= findViewById(R.id.relat1);
        relatline= findViewById(R.id.relatline);

        tvInicio=findViewById(R.id.tvInicio);
        //iv=findViewById(R.id.iv);
        myanim= AnimationUtils.loadAnimation(this,R.anim.mytransition);
        //iv.startAnimation(myanim);

        Handler handler= new Handler();
        Runnable runnable= new Runnable() {
            @Override
            public void run() {
                relat1.setVisibility(View.VISIBLE);
                tvInicio.startAnimation(myanim);
            }
        };

        Handler handler2= new Handler();
        Runnable runnable2= new Runnable() {
            @Override
            public void run() {
                relatline.setVisibility(View.VISIBLE);
                relatline.startAnimation(myanim);
            }
        };

        handler.postDelayed(runnable,2000);
        handler2.postDelayed(runnable2,3100);

        toSpeech = new TextToSpeech(Spalish.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status==TextToSpeech.SUCCESS)
                {
                    Locale locSpanish = new Locale("spa", "ECU");
                    //result=toSpeech.setLanguage(Locale.UK);
                    resultt=toSpeech.setLanguage(locSpanish);
                    toSpeech.speak("Bienvenido a ere and GO, app diseñada para atletas con discapacidad visual",TextToSpeech.QUEUE_FLUSH,null);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Caracteritica no soportada",Toast.LENGTH_SHORT).show();


                }
            }
        });

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
