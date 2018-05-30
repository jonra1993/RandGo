package com.e.jona.randgo;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.e.jona.randgo.DataHolder;

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener{

    Button b_cargar_gpx,b_carolina,b_estadiox, b_estadioy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        b_cargar_gpx=findViewById(R.id.b_cargar_gpx);
        b_cargar_gpx.setOnClickListener(this);
        b_carolina=findViewById(R.id.b_carolina);
        b_carolina.setOnClickListener(this);
        b_estadiox=findViewById(R.id.b_estadiox);
        b_estadiox.setOnClickListener(this);
        b_estadioy=findViewById(R.id.b_estadioy);
        b_estadioy.setOnClickListener(this);
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
                break;
            case R.id.b_estadiox:
                DataHolder.setData("CanchaEPN");
                break;
            case R.id.b_estadioy:
                DataHolder.setData("Estadioy");
                break;
        }
        this.finish();
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

