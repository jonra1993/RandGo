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
import static com.e.jona.randgo.DataHolder.getAuriculares_oseos;
import static com.e.jona.randgo.DataHolder.setAuriculares_oseos;

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener{

    Button b_cargar_gpx,b_carolina,b_estadiox, b_estadioy, btestadio3,btestadio4;
    TextToSpeech toSpeech;
    int resultt;
    public boolean [] mem;
    private CheckBox cbAuriculares_oseos, checkbox1;
    private Timer myTimer;
    int tiempo=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        mem= new boolean[6];
        mem[0]=false;
        mem[1]=false;
        mem[2]=false;
        mem[3]=false;
        mem[4]=false;
        mem[5]=false;
        b_cargar_gpx=findViewById(R.id.b_cargar_gpx);
        b_cargar_gpx.setOnClickListener(this);
        b_carolina=findViewById(R.id.b_carolina);
        b_carolina.setOnClickListener(this);
        b_estadiox=findViewById(R.id.b_estadiox);
        b_estadiox.setOnClickListener(this);
        b_estadioy=findViewById(R.id.b_estadioy);
        b_estadioy.setOnClickListener(this);
        btestadio3=findViewById(R.id.btestadio3);
        btestadio3.setOnClickListener(this);
        btestadio4=findViewById(R.id.btestadio4);
        btestadio4.setOnClickListener(this);

        checkbox1=findViewById(R.id.checkbox_audio);
        if(getData_Audio()) checkbox1.setChecked(true);
        else checkbox1.setChecked(false);

        cbAuriculares_oseos=findViewById(R.id.cdAuriculare_oseos);
        if(getAuriculares_oseos())cbAuriculares_oseos.setChecked(true);
        else cbAuriculares_oseos.setChecked(false);

        //Modificar ToolBar
        android.support.v7.widget.Toolbar toolbar_conf=findViewById(R.id.toolbar_conf);
        setSupportActionBar(toolbar_conf);
        getSupportActionBar().setIcon(R.drawable.ic_baraudiorun);


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
                    mem[4]=false;
                    mem[5]=false;
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

                if(mem[5]==true){
                    this.finish();
                    DataHolder.setData("GPX");
                    toSpeech.speak("Elija el Archivo",TextToSpeech.QUEUE_FLUSH,null);
                    if (checkbox1.isChecked()==true) setData_Audio(true);
                    else setData_Audio(false);
                    if (cbAuriculares_oseos.isChecked()==true) setAuriculares_oseos(true);
                    else setAuriculares_oseos(false);
                    break;
                }
                mem[0]=false;
                mem[1]=false;
                mem[2]=false;
                mem[3]=false;
                mem[4]=false;
                mem[5]=true;
                toSpeech.speak("Cargar archivo desde la memoria interna",TextToSpeech.QUEUE_FLUSH,null);
                break;
            case R.id.b_carolina:
                DataHolder.setData("Carolina");
                if(mem[0]==true){
                    this.finish();
                    toSpeech.speak(getString(R.string.tit2)+" cargado",TextToSpeech.QUEUE_FLUSH,null);
                    if (checkbox1.isChecked()==true) setData_Audio(true);
                    else setData_Audio(false);
                    if (cbAuriculares_oseos.isChecked()==true) setAuriculares_oseos(true);
                    else setAuriculares_oseos(false);
                    break;
                }
                mem[0]=true;
                mem[1]=false;
                mem[2]=false;
                mem[3]=false;
                mem[4]=false;
                mem[5]=false;
                toSpeech.speak(getString(R.string.tit2),TextToSpeech.QUEUE_FLUSH,null);
                break;
            case R.id.b_estadiox:
                DataHolder.setData("CanchaEPN");
                if(mem[1]==true){
                    this.finish();
                    toSpeech.speak(getString(R.string.tit3)+" cargado",TextToSpeech.QUEUE_FLUSH,null);
                    if (checkbox1.isChecked()==true) setData_Audio(true);
                    else setData_Audio(false);
                    if (cbAuriculares_oseos.isChecked()==true) setAuriculares_oseos(true);
                    else setAuriculares_oseos(false);
                break;
                }
                mem[0]=false;
                mem[1]=true;
                mem[2]=false;
                mem[3]=false;
                mem[4]=false;
                mem[5]=false;
                toSpeech.speak(getString(R.string.tit3),TextToSpeech.QUEUE_FLUSH,null);
                break;
            case R.id.b_estadioy:
                DataHolder.setData("Estadioy");
                if(mem[2]==true){
                    this.finish();
                    toSpeech.speak(getString(R.string.tit4)+" cargado",TextToSpeech.QUEUE_FLUSH,null);
                    if (checkbox1.isChecked()==true) setData_Audio(true);
                    else setData_Audio(false);
                    if (cbAuriculares_oseos.isChecked()==true) setAuriculares_oseos(true);
                    else setAuriculares_oseos(false);
                    break;
                }
                mem[0]=false;
                mem[1]=false;
                mem[2]=true;
                mem[3]=false;
                mem[4]=false;
                mem[5]=false;
                toSpeech.speak(getString(R.string.tit4),TextToSpeech.QUEUE_FLUSH,null);
                break;
            case R.id.btestadio3:
                DataHolder.setData("Estadioz");
                if (mem[3]==true){
                    this.finish();
                    toSpeech.speak("Estadio Alangasi"+" cargado",TextToSpeech.QUEUE_FLUSH,null);
                    if (checkbox1.isChecked()==true) setData_Audio(true);
                    else setData_Audio(false);
                    if (cbAuriculares_oseos.isChecked()==true) setAuriculares_oseos(true);
                    else setAuriculares_oseos(false);
                    break;
                }
                mem[0]=false;
                mem[1]=false;
                mem[2]=false;
                mem[3]=true;
                mem[4]=false;
                mem[5]=false;
                toSpeech.speak("Estadio Alangasi",TextToSpeech.QUEUE_FLUSH,null);
                break;

            case R.id.btestadio4:
                DataHolder.setData("Estadiozz");
                if (mem[4]==true){
                    this.finish();
                    toSpeech.speak("Estadio Alangasi 2"+" cargado",TextToSpeech.QUEUE_FLUSH,null);
                    if (checkbox1.isChecked()==true) setData_Audio(true);
                    else setData_Audio(false);
                    if (cbAuriculares_oseos.isChecked()==true) setAuriculares_oseos(true);
                    else setAuriculares_oseos(false);
                    break;
                }
                mem[0]=false;
                mem[1]=false;
                mem[2]=false;
                mem[3]=false;
                mem[4]=true;
                mem[5]=false;
                toSpeech.speak("Estadio Alangasi 2",TextToSpeech.QUEUE_FLUSH,null);
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
                mem[4]=false;
                mem[5]=false;
            }
            else{
                this.finish();
                mem[3]=false;
                mem[0]=false;
                mem[1]=false;
                mem[2]=false;
                mem[4]=false;
                mem[5]=false;
                if (cbAuriculares_oseos.isChecked()==true) setAuriculares_oseos(true);
                else setAuriculares_oseos(false);

            }
        }
        return super.onOptionsItemSelected(item);
    }
}


