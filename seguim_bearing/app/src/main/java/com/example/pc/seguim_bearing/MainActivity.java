package com.example.pc.seguim_bearing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener, GPXListeners.GPXParserListener, GPXListeners.GPXParserProgressListener  {
    private Uri gpxURI;
    private GPXParser mParser; //Variable para cargar gpx
    private ProgressDialog mProgressDialog = null;
    private List<GPXTrackPoint> mPoints = null;         //Lista de puntos gpx

    ListView lwPuntosGPS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lwPuntosGPS=(ListView) findViewById(R.id.lwPuntosGPS);

        cargargpx("carolina.gpx");
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

    }

    @Override
    public void onGpxParseCompleted(GPXDocument document) {
        mProgressDialog.dismiss();

        mPoints=document.getTracks().get(0).getSegments().get(0).getTrackPoints(); //Poner puntos en la lista

        ArrayList<GeoPoint> puntos = new ArrayList<GeoPoint>();                     //Lista de puntos geograficos
        for (int i = 0; i<(mPoints.size()); i++)
        {
            GeoPoint t = new GeoPoint(mPoints.get(i).getLatitude(), mPoints.get(i).getLongitude());
            puntos.add(t);
        }

        lwPuntosGPS.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mPoints.size();
            }

            @Override
            public Object getItem(int position) {
                return mPoints.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup vg) {
                final LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v=convertView;
                if (v==null) v=inflater.inflate(R.layout.puntos_gps,vg,false);

                GPXBasePoint t= (GPXBasePoint) getItem(position);

                TextView tvNumero= v.findViewById(R.id.tvNumero);
                TextView tvLat_Long= v.findViewById(R.id.tvLat_Long);
                TextView tvBearing= v.findViewById(R.id.tvLBearing);

                tvNumero.setText("Punto: "+t.getName());
                tvLat_Long.setText("Lati: "+t.getLatitude()+", Lon: "+t.getLongitude());

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

