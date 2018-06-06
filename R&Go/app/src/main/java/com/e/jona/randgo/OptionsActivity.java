package com.e.jona.randgo;


import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.e.jona.randgo.DataHolder;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.e.jona.randgo.DataHolder.getData_Audio;
import static com.e.jona.randgo.DataHolder.setData_Audio;

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener{

    Button b_cargar_gpx,b_carolina,b_estadiox, b_estadioy;
    EditText etP,etI,etD;
    TextToSpeech toSpeech;
    int resultt;
    public boolean [] mem;
    private CheckBox checkbox1;
    private Timer myTimer;
    int tiempo=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        etP=findViewById(R.id.etP);
        etI=findViewById(R.id.etI);
        etD=findViewById(R.id.etD);

        etP.setText((String.format("%.2f",DataHolder.getPID_P())).replace(',','.'));
        etI.setText(String.format("%.2f",DataHolder.getPID_I()).replace(',','.'));
        etD.setText(String.format("%.2f",DataHolder.getPID_D()).replace(',','.'));


        mem= new boolean[4];
        mem[0]=false;
        mem[1]=false;
        mem[2]=false;
        mem[3]=false;
        b_cargar_gpx=findViewById(R.id.b_cargar_gpx);
        b_cargar_gpx.setOnClickListener(this);
        b_carolina=findViewById(R.id.b_carolina);
        b_carolina.setOnClickListener(this);
        b_estadiox=findViewById(R.id.b_estadiox);
        b_estadiox.setOnClickListener(this);
        b_estadioy=findViewById(R.id.b_estadioy);
        b_estadioy.setOnClickListener(this);

        checkbox1=(CheckBox)findViewById(R.id.checkbox_audio);
        if(getData_Audio()) checkbox1.setChecked(true);
        else checkbox1.setChecked(false);




        toSpeech = new TextToSpeech(OptionsActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status==TextToSpeech.SUCCESS)
                {
                    Locale locSpanish = new Locale("spa", "ECU");
                    //result=toSpeech.setLanguage(Locale.UK);
                    resultt=toSpeech.setLanguage(locSpanish);
                    toSpeech.speak("Elija la pista que desee cargar",TextToSpeech.QUEUE_FLUSH,null);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Caracteritica no soportada",Toast.LENGTH_SHORT).show();


                }
            }
        });

        myTimer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {

                tiempo++; if(tiempo>3){
                    mem[3]=false;
                    mem[0]=false;
                    mem[1]=false;
                    mem[2]=false;
                    tiempo=0;
                }

            }
        };
        myTimer.scheduleAtFixedRate(t,0,1000);

    }


    @Override
    public void onClick(View view) {
        tiempo=0;
        switch (view.getId())
        {
            case R.id.b_cargar_gpx:
                DataHolder.setData("GPX");
                break;
            case R.id.b_carolina:
                DataHolder.setData("Carolina");
                if(mem[0]==true){
                    this.finish();
                    toSpeech.speak(getString(R.string.tit2)+" cargado",TextToSpeech.QUEUE_FLUSH,null);
                    if (checkbox1.isChecked()==true) setData_Audio(true);
                    else setData_Audio(false);
                    break;
                }
                mem[0]=true;
                mem[1]=false;
                mem[2]=false;
                mem[3]=false;
                toSpeech.speak(getString(R.string.tit2),TextToSpeech.QUEUE_FLUSH,null);
                break;
            case R.id.b_estadiox:
                DataHolder.setData("CanchaEPN");
                if(mem[1]==true){
                    this.finish();
                    toSpeech.speak(getString(R.string.tit3)+" cargado",TextToSpeech.QUEUE_FLUSH,null);
                    if (checkbox1.isChecked()==true) setData_Audio(true);
                    else setData_Audio(false);
                    break;
                }
                mem[0]=false;
                mem[1]=true;
                mem[2]=false;
                mem[3]=false;
                toSpeech.speak(getString(R.string.tit3),TextToSpeech.QUEUE_FLUSH,null);
                break;
            case R.id.b_estadioy:
                DataHolder.setData("Estadioy");
                if(mem[2]==true){
                    this.finish();
                    toSpeech.speak(getString(R.string.tit4)+" cargado",TextToSpeech.QUEUE_FLUSH,null);
                    if (checkbox1.isChecked()==true) setData_Audio(true);
                    else setData_Audio(false);
                    break;
                }
                mem[0]=false;
                mem[1]=false;
                mem[2]=true;
                mem[3]=false;
                toSpeech.speak(getString(R.string.tit4),TextToSpeech.QUEUE_FLUSH,null);
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
            tiempo=0;
            if(mem[3]==false){
                toSpeech.speak("Regresar a la ventana principal",TextToSpeech.QUEUE_FLUSH,null);
                mem[3]=true;
                mem[0]=false;
                mem[1]=false;
                mem[2]=false;
            }
            else{
                this.finish();
                mem[3]=false;
                mem[0]=false;
                mem[1]=false;
                mem[2]=false;

            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        DataHolder.setPID(Float.parseFloat(etP.getText().toString()),Float.parseFloat(etI.getText().toString()),Float.parseFloat(etD.getText().toString()));
    }

}


