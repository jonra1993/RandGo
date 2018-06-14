package com.example.pc.seguim_bearing;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codebutchery.androidgpx.data.GPXBasePoint;
import com.codebutchery.androidgpx.data.GPXDocument;
import com.codebutchery.androidgpx.data.GPXRoute;
import com.codebutchery.androidgpx.data.GPXRoutePoint;
import com.codebutchery.androidgpx.data.GPXSegment;
import com.codebutchery.androidgpx.data.GPXTrack;
import com.codebutchery.androidgpx.data.GPXTrackPoint;
import com.codebutchery.androidgpx.data.GPXWayPoint;
import com.codebutchery.androidgpx.xml.GPXListeners;
import com.codebutchery.androidgpx.xml.GPXParser;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener, GPXListeners.GPXParserListener, GPXListeners.GPXParserProgressListener  {

    LocationManager locationManager;
    private Location location;
    private Location posi_act=new Location(LocationManager.GPS_PROVIDER);
    private Location posi_sig=new Location(LocationManager.GPS_PROVIDER);
    private Location posi_ant=new Location(LocationManager.GPS_PROVIDER);


    int posit=0;

    private List<TrackPointsActivity> items = new ArrayList<TrackPointsActivity>();

    private List<GPXTrackPoint> mPoints = null;
    ListView lwPuntosGPS;

    private GPXParser mParser;


    private ProgressDialog mProgressDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        lwPuntosGPS=(ListView) findViewById(R.id.lwPuntosGPS);

        cargargpx("CanchaEPN3.gpx");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }


    }

    ////Metodos de Location Manager
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //Metodos de pulsador
    @Override
    public void onClick(View v) {

    }


    ///Metodos de gpx
    @Override
    public void onGpxParseStarted() {
        mProgressDialog = ProgressDialog.show(this, "Parsing GPX", "Started");

    }

    @Override
    public void onGpxParseCompleted(GPXDocument document) {
        mProgressDialog.dismiss();
        float val_bearing;
        float val_bearing_ant;
        float val_distancia;

        mPoints=document.getTracks().get(0).getSegments().get(0).getTrackPoints(); //Poner puntos en la lista

        for (int i = 0; i<(mPoints.size()); i++)
        {
            if(i<(mPoints.size()-1)){
                if(i==0){
                    posi_ant.setLatitude(mPoints.get(mPoints.size()-1).getLatitude());
                    posi_ant.setLongitude(mPoints.get(mPoints.size()-1).getLongitude());
                }
                else {
                    posi_ant.setLatitude(mPoints.get(i-1).getLatitude());
                    posi_ant.setLongitude(mPoints.get(i-1).getLongitude());
                }

                posi_act.setLatitude(mPoints.get(i).getLatitude());
                posi_act.setLongitude(mPoints.get(i).getLongitude());

                posi_sig.setLatitude(mPoints.get(i+1).getLatitude());
                posi_sig.setLongitude(mPoints.get(i+1).getLongitude());
            }
            else {
                posi_ant.setLatitude(mPoints.get(i-1).getLatitude());
                posi_ant.setLongitude(mPoints.get(i-1).getLongitude());

                posi_act.setLatitude(mPoints.get(i).getLatitude());
                posi_act.setLongitude(mPoints.get(i).getLongitude());

                posi_sig.setLatitude(mPoints.get(0).getLatitude());
                posi_sig.setLongitude(mPoints.get(0).getLongitude());
            }

            val_bearing=posi_act.bearingTo(posi_sig);
            val_distancia=posi_act.distanceTo(posi_sig);
            val_bearing_ant=posi_act.bearingTo(posi_ant);
            if(val_bearing_ant<0.0f) val_bearing_ant+=360;


            TrackPointsActivity coord= new TrackPointsActivity(i,mPoints.get(i).getLatitude(), mPoints.get(i).getLongitude(),val_bearing , val_distancia,val_bearing_ant);
            items.add(coord);
        }

        lwPuntosGPS.setAdapter(new BaseAdapter() {

            @Override
            public int getCount() {
                //posit=mPoints.size();
                return items.size();
            }

            @Override
            public Object getItem(int arg0) {
                posit++;
                return items.get(arg0);
            }

            @Override
            public long getItemId(int arg0) {
                return arg0;
            }

            @Override
            public View getView(int arg0, View recycled, ViewGroup vg) {

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = recycled;
                if (v == null) v = inflater.inflate(R.layout.puntos_gps, vg, false);

                TrackPointsActivity currentItem = (TrackPointsActivity) getItem(arg0);

                TextView tvNumero = v.findViewById(R.id.tvNumero);
                TextView tvLat_Long = v.findViewById(R.id.tvLat_Long);
                TextView tvBearing = v.findViewById(R.id.tvLBearing);
                TextView tvDistancia = v.findViewById(R.id.tvDistancia);
                TextView tvBearing_ant=v.findViewById(R.id.tvBearing_ant);

                tvNumero.setText(String.format("Punto: %d", currentItem.getitemNumber()));
                tvLat_Long.setText(String.format("Lati: %f, Long: %f",currentItem.getitemLatitud(),currentItem.getitemLongitud()));
                tvBearing.setText(String.format("Bearing : %f",currentItem.getitemBearing()));
                tvDistancia.setText(String.format("Distancia alsig punto: %f",currentItem.getItemDistancia()));
                tvBearing_ant.setText(String.format("Bearing ant: %.2f",currentItem.getitemBearing_ant()));

                return v;
            }
        });


    }

    @Override
    public void onGpxParseError(String type, String message, int lineNumber, int columnNumber) {
        mProgressDialog.dismiss();

        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("An error occurred: " + message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void onGpxNewTrackParsed(int count, GPXTrack track) {

    }

    @Override
    public void onGpxNewRouteParsed(int count, GPXRoute track) {

    }

    @Override
    public void onGpxNewSegmentParsed(int count, GPXSegment segment) {

    }

    @Override
    public void onGpxNewTrackPointParsed(int count, GPXTrackPoint trackPoint) {

    }

    @Override
    public void onGpxNewRoutePointParsed(int count, GPXRoutePoint routePoint) {

    }

    @Override
    public void onGpxNewWayPointParsed(int count, GPXWayPoint wayPoint) {

    }

    ///Metodods adicionales
    private void cargargpx(String path) {
        try {
            InputStream input = getAssets().open(path);
            // The GpxParser automatically closes the InputStream so we do not have to bother about it
            mParser = new GPXParser(this, this);
            mParser.parse(input);
        } catch (IOException e) {
            Toast.makeText(this, "IOExeption opening file", Toast.LENGTH_SHORT).show();
        }
    }
}

