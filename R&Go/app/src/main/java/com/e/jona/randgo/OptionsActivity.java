package com.e.jona.randgo;


import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.e.jona.randgo.DataHolder;

import java.util.Locale;

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener{

    Button b_cargar_gpx,b_carolina,b_estadiox, b_estadioy;
    TextToSpeech toSpeech;
    int resultt;
    boolean [] mem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        mem= new boolean[3];
        mem[0]=false;
        mem[1]=false;
        mem[2]=false;
        b_cargar_gpx=findViewById(R.id.b_cargar_gpx);
        b_cargar_gpx.setOnClickListener(this);
        b_carolina=findViewById(R.id.b_carolina);
        b_carolina.setOnClickListener(this);
        b_estadiox=findViewById(R.id.b_estadiox);
        b_estadiox.setOnClickListener(this);
        b_estadioy=findViewById(R.id.b_estadioy);
        b_estadioy.setOnClickListener(this);

        toSpeech = new TextToSpeech(OptionsActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status==TextToSpeech.SUCCESS)
                {
                    Locale locSpanish = new Locale("spa", "ECU");
                    //result=toSpeech.setLanguage(Locale.UK);
                    resultt=toSpeech.setLanguage(locSpanish);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Caracteritica no soportada",Toast.LENGTH_SHORT).show();


                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.b_cargar_gpx:
                DataHolder.setData("GPX");
                break;
            case R.id.b_carolina:
                DataHolder.setData("Carolina");
                if(mem[0]==true) this.finish();
                mem[0]=true;
                mem[1]=false;
                mem[2]=false;
                toSpeech.speak(String.valueOf(R.string.tit2),TextToSpeech.QUEUE_FLUSH,null);
                break;
            case R.id.b_estadiox:
                DataHolder.setData("CanchaEPN");
                if(mem[1]==true) this.finish();
                mem[0]=false;
                mem[1]=true;
                mem[2]=false;
                toSpeech.speak(String.valueOf(R.string.tit3),TextToSpeech.QUEUE_FLUSH,null);
                break;
            case R.id.b_estadioy:
                DataHolder.setData("Estadioy");
                if(mem[2]==true) this.finish();
                mem[0]=false;
                mem[1]=false;
                mem[2]=true;
                toSpeech.speak(String.valueOf(R.string.tit4),TextToSpeech.QUEUE_FLUSH,null);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.item2){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

