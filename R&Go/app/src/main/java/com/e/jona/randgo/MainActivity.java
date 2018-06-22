package com.e.jona.randgo;
//https://github.com/MKergall/osmbonuspack/wiki/Tutorial_5
//https://github.com/osmdroid/osmdroid/wiki
//doc https://osmdroid.github.io/osmdroid/javadocs/osmdroid-android/debug/org/osmdroid/views/overlay/mylocation/MyLocationNewOverlay.html
// https://www.programcreek.com/java-api-examples/index.php?source_dir=osmdroid-master/osmdroid-android/src/main/java/org/osmdroid/views/overlay/MyLocationOverlay.java
// https://code.google.com/archive/p/osmdroid/source/default/source
//https://www.movable-type.co.uk/scripts/latlong.html
//https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
//https://memorynotfound.com/calculating-elapsed-time-java/

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.codebutchery.androidgpx.data.GPXDocument;
import com.codebutchery.androidgpx.data.GPXRoute;
import com.codebutchery.androidgpx.data.GPXRoutePoint;
import com.codebutchery.androidgpx.data.GPXSegment;
import com.codebutchery.androidgpx.data.GPXTrack;
import com.codebutchery.androidgpx.data.GPXTrackPoint;
import com.codebutchery.androidgpx.data.GPXWayPoint;
import com.codebutchery.androidgpx.xml.GPXListeners;
import com.codebutchery.androidgpx.xml.GPXParser;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import com.e.jona.randgo.DataHolder;
import java.util.concurrent.TimeUnit;
import java.util.*;

import static com.e.jona.randgo.DataHolder.getAuriculares_oseos;
import static com.e.jona.randgo.DataHolder.getData_Audio;
import static com.e.jona.randgo.DataHolder.setAuriculares_oseos;
import static com.e.jona.randgo.DataHolder.setData_Audio;

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener, GPXListeners.GPXParserListener, GPXListeners.GPXParserProgressListener  {

    int on=0;
    boolean comenzar=false;
    private Timer myTimer, myTimer2;
    MapView map = null;
    IMapController mapController;
    LocationManager locationManager;
    MyLocationNewOverlay mLocationOverlay;
    public CompassOverlay compassOverlay;
    private Location location;
    FloatingActionButton btnMyLocation,btnNavigation;
    private Uri gpxURI;
    private GPXParser mParser;
    private static final int PICKER=1;
    private ProgressDialog mProgressDialog = null;
    protected Polygon mDestinationPolygon;
    private float direction;
    long millis_before, millis_after, fin_km, inic_km, millis, elapsed;
    Date startDateTime, endDateTime, finDate_km, inicDate_km;
    TextToSpeech toSpeech;
    int resultt;
    float bearing_actual=500;
    int tiempo=0;

    //Cargar gpx
    private List<TrackPointsActivity> items = new ArrayList<TrackPointsActivity>();
    private Location posi_act=new Location(LocationManager.GPS_PROVIDER);
    private Location posi_sig=new Location(LocationManager.GPS_PROVIDER);
    private Location posi_ant=new Location(LocationManager.GPS_PROVIDER);
    private Location sig_paso=new Location(LocationManager.GPS_PROVIDER);

    //Medicion de distancia
    double lat_actual, lon_actual, lat_ant, lon_ant;
    float dist = 0;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // in Meters
    private static final long MIN_TIME_BW_UPDATES = 20;
    private static final int lim_accur_gps=15;

    MediaPlayer mp;
    //PID pid;


    TextView tvDistancia, tvPresicionGPS, tvPrueba, tvPres, tvLimites;
    boolean [] mem;
    boolean me2;
    private static int conta, conta_km;
    private int index;

    float teta1,aux_idex;
    double teta2, lim_ang_min, lim_ang_max;
    int cuadr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataHolder.setData("NULL");
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);

        //Modificar ToolBar
        android.support.v7.widget.Toolbar my_toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(my_toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_baraudiorun);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission(this);
        }

        //conectar layout
        btnMyLocation = findViewById(R.id.btnMyLocation);
        btnMyLocation.setOnClickListener(this);
        btnNavigation=findViewById(R.id.btnNavigation);
        btnNavigation.setOnClickListener(this);
        btnNavigation.hide();

        tvDistancia= findViewById(R.id.tvDistancia);
        tvDistancia.setText(String.format("#: %d",index));
        tvPresicionGPS=findViewById(R.id.tvPresicionGPS);
        tvPrueba=findViewById(R.id.tvPrueba);
        tvPres=findViewById(R.id.tvPres);
        tvLimites=findViewById(R.id.tvLimites);

        mem= new boolean[3];
        mem[0]=false;mem[1]=false;
        me2=false;
        conta=0;
        conta_km=0;
        index=0;

        setData_Audio(true);
        setAuriculares_oseos(true);

        mp=MediaPlayer.create(this,R.raw.sinsilencio);
        mp.setLooping(true);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getOverlayManager().getTilesOverlay().setColorFilter(null);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setMaxZoomLevel(20.0);
        mapController = map.getController();
        mapController.setZoom(19.0);

        //configuraciones de localización
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        mLocationOverlay.getLastFix();
        mLocationOverlay.setDrawAccuracyEnabled(true);
        map.getOverlays().add(this.mLocationOverlay);
        map.postInvalidate();

        //añadir la brújula
        compassOverlay=new CompassOverlay(this,map);
        map.getOverlays().add(compassOverlay);
        compassOverlay.enableCompass();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                updateLocation(location);
            }
        }
        myTimer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                tiempo++; if(tiempo>5){
                    mem[0]=false;mem[1]=false;
                    me2=false;
                    tiempo=0;
                }

                switch (DataHolder.getData())
                {
                    case "GPX":
                        FilePicker();
                        DataHolder.setData("NULL");
                        break;
                    case "Carolina":
                        cargargpx("carolina2.gpx");
                        DataHolder.setData("NULL");

                        break;
                    case "CanchaEPN":
                        cargargpx("CanchaEPN3.gpx");
                        DataHolder.setData("NULL");

                        break;
                    case "Estadioy":
                        cargargpx("esmil2.gpx");
                        DataHolder.setData("NULL");
                        break;
                    case "Estadioz":
                        cargargpx("alangasi.gpx");
                        DataHolder.setData("NULL");
                        break;
                    case "Estadiozz":
                        cargargpx("alangasi2.gpx");
                        DataHolder.setData("NULL");
                        break;
                }
            }
        };
        myTimer.scheduleAtFixedRate(t,0,1000);

        TimerTask tt = new TimerTask() {
            @Override
            public void run() { ;
            float copy=bearing_actual;
                if(copy!=500&&comenzar==true&&getData_Audio())
                {
                    if (index<(items.size()-1)){
                        sig_paso.setLatitude(items.get(index+1).getitemLatitud());
                        sig_paso.setLongitude(items.get(index+1).getitemLongitud());
                    }
                    else {
                        sig_paso.setLatitude(items.get(0).getitemLatitud());
                        sig_paso.setLongitude(items.get(0).getitemLongitud());
                        index=0;
                    }
                    float ref= items.get(index).getitemBearing();

                    //Algoritmo parar seleccion de nuevo punto en la pista
                    teta1=items.get(index).get_teta();                               //Angulo bearing al punto anterior
                    teta2=Math.toDegrees(Math.atan(-1/(Math.toRadians(teta1))));      //angulo ortogonal de ref_sigPunto
                    aux_idex=sig_paso.bearingTo(location);
                    if(aux_idex<0) aux_idex+=360;

                    //le cambié estaban mal los casos  y no entiendo
                    if(teta1>=0 && teta1<90) { //primer cuadrante
                        cuadr=1;
                        lim_ang_min= 360+teta2;
                        lim_ang_max=180+teta2;
                        if(!(aux_idex>360+teta2 || aux_idex<180+teta2)) index++;
                    }
                    else if(teta1>=90 && teta1<180){ //cuarto cuadrante
                        cuadr=4;
                        lim_ang_min= teta2;
                        lim_ang_max=180+teta2;
                        if(!(aux_idex>teta2 && aux_idex<180+teta2)) index++;
                    }
                    else if (teta1>=180 && teta1<270){ //tercer cuadrante
                        cuadr=3;
                        lim_ang_min= 180+teta2;
                        lim_ang_max=360+teta2;
                        if(!(aux_idex>180+teta2 && aux_idex<360+teta2)) index++;
                    }
                    else{ //segundo cuadrante
                        cuadr=2;
                        lim_ang_min= 180+teta2;
                        lim_ang_max=teta2;
                        if(!(aux_idex>180+teta2 || aux_idex<teta2)) index++;
                    }

                   //Controlador
                    float[] temp = funcion_sonido_controlador(copy,ref,-5,5);
                    Log.d("Volumen r", String.valueOf(temp[0]));
                    Log.d("Volumen l", String.valueOf(temp[1]));
                    mp.setVolume(temp[1], temp[0]);
                }
                else{
                    mp.setVolume((float)0.0, (float)0.0);
                }
            }
        };
        myTimer2 = new Timer();
        myTimer2.scheduleAtFixedRate(tt,0,60);

        toSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status==TextToSpeech.SUCCESS)
                {
                    Locale locSpanish = new Locale("spa", "ECU");
                    resultt=toSpeech.setLanguage(locSpanish);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Caracteritica no soportada",Toast.LENGTH_SHORT).show();


                }
            }
        });
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Es necesario habilitar el GPS para el uso correcto de esta aplicacion")
                    .setCancelable(false)
                    .setNegativeButton("Encender GPS", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.item1){
            if(comenzar==false){
                if(me2==true){
                    Intent intent = new Intent(MainActivity.this,OptionsActivity.class);
                    startActivity(intent);
                    me2=false;
                }
                else{
                    toSpeech.speak("Menú de Opciones",TextToSpeech.QUEUE_FLUSH,null);
                    me2=true;
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume(){
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        requestMyLocation();

        map.onResume();
        mLocationOverlay.getLastFix();
        mLocationOverlay.enableMyLocation();


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean requestPermission(Context context) {
        boolean flag = true;
        int fineLocation = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocation = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int writeStorage = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readStorage = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> permissions = new ArrayList<>();
        if (fineLocation != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (coarseLocation != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (writeStorage != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (readStorage != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        String[] permis = permissions.toArray(new String[permissions.size()]);
        if (permis.length > 0) {
            flag = false;
            requestPermissions(permis, 0);
        }
        return flag;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(Location location) {
        //updateLocation(location);
        this.location=location;
        if (on==0){
            updateLocation(location);
            on=1;
        }
        if (location.hasBearing()){
            if(comenzar==true) {
                calculo_distancia(location);
                bearing_actual=location.getBearing();
                tvPresicionGPS.setText(String.format("Bant: %.2f",teta1));
                tvDistancia.setText(String.format("#: %d", index));
                tvPrueba.setText(String.format("Bm : %.2f",aux_idex));
                tvPres.setText(String.format(" %.2f",teta2));
                tvLimites.setText(String.format("L: Cuadr: %d, Desde %.2f, hasta %.2f",cuadr,lim_ang_min,lim_ang_max ));
            }
        }
        else{
            bearing_actual=500;
        }

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

    private void updateLocation(Location location) {
        GeoPoint locGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.setCenter(locGeoPoint);
       // Toast.makeText(getBaseContext(),locGeoPoint.getLatitude() + " - "+locGeoPoint.getLongitude(),Toast.LENGTH_SHORT).show();
        map.invalidate();
        if (location.hasBearing()==true) {
            direction=location.getBearing();
           // Toast.makeText(getBaseContext(),""+direction+"o="+compassOverlay.getOrientation(),Toast.LENGTH_SHORT).show();
        } else {
                   //no se ha cambiado la posición
          //  Toast.makeText(getBaseContext(),"no tiene bearing"+"o="+compassOverlay.getOrientation(),Toast.LENGTH_SHORT).show();
        }
    }

    private void calculo_distancia (Location location){
        lat_actual = location.getLatitude();
        lon_actual = location.getLongitude();

        if(comenzar==true){
            float[] results = new float[1];
            Location.distanceBetween(lat_ant, lon_ant, lat_actual, lon_actual, results);

            if (location.getAccuracy()<=lim_accur_gps && results[0]<=10){
                Location.distanceBetween(lat_ant, lon_ant, lat_actual, lon_actual, results);
                dist = dist + results[0];
            }

            lat_ant = lat_actual;
            lon_ant = lon_actual;

            if (dist>=(conta+1)*100){
                if (dist>=(conta_km+1)*1000){
                    alarta_km((int)(conta_km+1));
                    conta_km++;
                    conta++;
                }
                else {
                    verb_distancia((int) (conta + 1) * 100);
                    conta++;
                }
            }


        }
    }

    private void verb_distancia(int val_distancia){
        String tempo3= String.format("Usted a recorrido %d metros",val_distancia);
        toSpeech.speak(tempo3,TextToSpeech.QUEUE_FLUSH,null);
        toSpeech.playSilence(1000,TextToSpeech.QUEUE_ADD,null);
    }

    private void alarta_km (int d){
        fin_km = System.currentTimeMillis();
        finDate_km = new Date(fin_km);
        inicDate_km= new Date(inic_km);
        Map<TimeUnit,Long> result = computeDiff(inicDate_km, finDate_km);

        String tempo= String.format("%d minutos %d segundos",result.get(TimeUnit.MINUTES),result.get(TimeUnit.SECONDS));
        String tempo1= String.format("Kilometro %d, ritmo por kilometro"+tempo,d);

        toSpeech.speak(tempo1,TextToSpeech.QUEUE_FLUSH,null);
        toSpeech.playSilence(1000,TextToSpeech.QUEUE_ADD,null);

        inic_km=System.currentTimeMillis();
    }


    private void requestMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

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


    @Override
    public void onGpxNewRouteParsed(int count, GPXRoute track) {
        mProgressDialog.setMessage("Finished parsing route " + track.getName());
    }

    @Override
    public void onGpxNewRoutePointParsed(int count, GPXRoutePoint routePoint) {

    }

    @Override
    public void onGpxNewTrackParsed(int count, GPXTrack track) {
        mProgressDialog.setMessage("Finished parsing track " + track.getName());
    }

    @Override
    public void onGpxNewSegmentParsed(int count, GPXSegment segment) {
        mProgressDialog.setMessage("Parsing track segment " + count);
    }

    @Override
    public void onGpxNewTrackPointParsed(int count, GPXTrackPoint trackPoint) {

    }

    @Override
    public void onGpxNewWayPointParsed(int count, GPXWayPoint wayPoint) {

    }

    @Override
    public void onGpxParseStarted() {
        mProgressDialog = ProgressDialog.show(this, "Parsing GPX", "Started");
    }

    @Override
    public void onGpxParseCompleted(GPXDocument document) {
        mProgressDialog.dismiss();
        float val_bearing;
        float teta;
        float val_distancia;

        items.clear();              //Limpiar Lista de calse

        List<GPXTrackPoint> mPoints = null;
        mPoints=document.getTracks().get(0).getSegments().get(0).getTrackPoints();
        ArrayList<GeoPoint> puntos = new ArrayList<GeoPoint>();
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
                posi_act.setLatitude(mPoints.get(i).getLatitude());
                posi_act.setLongitude(mPoints.get(i).getLongitude());

                posi_sig.setLatitude(mPoints.get(0).getLatitude());
                posi_sig.setLongitude(mPoints.get(0).getLongitude());
            }

            val_bearing=posi_act.bearingTo(posi_sig);
            if(val_bearing<0) val_bearing+=360;
            val_distancia=posi_act.distanceTo(posi_sig);

            //deberia ser posi_sig to posic_actual solo al reves
            teta=posi_sig.bearingTo(posi_act);
            if(teta<0.0f) teta+=360;

            TrackPointsActivity coord= new TrackPointsActivity(i,mPoints.get(i).getLatitude(), mPoints.get(i).getLongitude(),val_bearing , val_distancia, teta);
            items.add(coord);

            GeoPoint t = new GeoPoint(mPoints.get(i).getLatitude(), mPoints.get(i).getLongitude());
            puntos.add(t);
        }
        updateUIWithPolygon(puntos,"ruta1");
        btnNavigation.show();
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
    public void updateUIWithPolygon(ArrayList<GeoPoint> polygon, String name){
        List<Overlay> mapOverlays = map.getOverlays();
        int location = -1;
        if (mDestinationPolygon != null)
            location = mapOverlays.indexOf(mDestinationPolygon);
        mDestinationPolygon = new Polygon();
        mDestinationPolygon.setFillColor(0x15FF0080);
        mDestinationPolygon.setStrokeColor(0x800000FF);
        mDestinationPolygon.setStrokeWidth(5.0f);
        mDestinationPolygon.setTitle(name);
        BoundingBox bb = null;
        if (polygon != null){
            mDestinationPolygon.setPoints(polygon);
            bb = BoundingBox.fromGeoPoints(polygon);
        }
        if (location != -1)
            mapOverlays.set(location, mDestinationPolygon);
        else
            mapOverlays.add(1, mDestinationPolygon); //insert just above the MapEventsOverlay.
        setViewOn(bb);
        mapController.setZoom(map.getZoomLevelDouble()-map.getZoomLevelDouble()*0.02);

        map.invalidate();
    }
    void setViewOn(BoundingBox bb){
        if (bb != null){
            map.zoomToBoundingBox(bb, false);
        }
    }
    private void FilePicker() {
        Intent target=new Intent(Intent.ACTION_GET_CONTENT);
        target.setType("file/*");
        target.addCategory(Intent.CATEGORY_OPENABLE);
        try{
            startActivityForResult(Intent.createChooser(target,"Seleccionar archivo"),PICKER);
        }catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(this, "Instale un administrador de archivos.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICKER:
                if (resultCode == RESULT_OK) {
                    //FilePath = data.getData().getPath();
                    gpxURI = data.getData();
                    try {
                        InputStream in = getContentResolver().openInputStream(gpxURI);
                        mParser = new GPXParser(this, this);
                        mParser.parse(in);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, "IOExeption opening file", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        tiempo=0;
        switch (v.getId()) {
            case R.id.btnMyLocation:
                requestMyLocation();
                if(location!=null){
                    //Location lastFix=mLocationOverlay.getLastFix();
                    updateLocation(location);
                    toSpeech.speak("Actualizando mí ubicación",TextToSpeech.QUEUE_FLUSH,null);
                    mem[0]=false;mem[1]=false;
                    me2=false;
                }
                break;

            case R.id.btnNavigation:
                if(comenzar==false){
                    if(mem[0]==false){
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                            toSpeech.speak("Botón de inicio de carrera",TextToSpeech.QUEUE_FLUSH,null);
                            mem[0]=true;mem[1]=false;
                            me2=false;
                        }
                        else{
                            toSpeech.speak("Encender el GPS para continuar",TextToSpeech.QUEUE_FLUSH,null);
                        }
                    }
                    else{
                        mem[0]=false;mem[1]=false;
                        me2=false;

                        if (resultt==TextToSpeech.LANG_MISSING_DATA||resultt==TextToSpeech.LANG_NOT_SUPPORTED) Toast.makeText(getApplicationContext(),"TTS no soportado", Toast.LENGTH_SHORT).show();
                        else{
                            toSpeech.speak("La carrera comienza en 3",TextToSpeech.QUEUE_FLUSH,null);
                            toSpeech.playSilence(400,TextToSpeech.QUEUE_ADD,null);
                            toSpeech.speak("2",TextToSpeech.QUEUE_ADD,null);
                            toSpeech.playSilence(400,TextToSpeech.QUEUE_ADD,null);
                            toSpeech.speak("1",TextToSpeech.QUEUE_ADD,null);
                            toSpeech.playSilence(400,TextToSpeech.QUEUE_ADD,null);
                            toSpeech.speak("Ahora",TextToSpeech.QUEUE_ADD,null);
                        }
                        //Toast.makeText(this, "La carrera comienza Ahora", Toast.LENGTH_SHORT).show();
                        millis_before = System.currentTimeMillis();
                        inic_km=millis_before;
                        startDateTime = new Date(millis_before);
                        mp.start();
                        comenzar=true;
                        conta=0;
                        conta_km=0;
                        index=near(items, location);
                        //pid.setPID(DataHolder.getPID_P(),DataHolder.getPID_I(),DataHolder.getPID_D());

                    }
                }
                else{
                    if(mem[1]==false){
                        toSpeech.speak("Botón de finalización de carrera",TextToSpeech.QUEUE_FLUSH,null);
                        mem[0]=false;mem[1]=true;
                        me2=false;
                    }
                    else{
                        mem[0]=false;mem[1]=false;
                        me2=false;
                        mp.pause();
                        millis_after = System.currentTimeMillis();
                        endDateTime = new Date(millis_after);
                        Map<TimeUnit,Long> result = computeDiff(startDateTime, endDateTime);
                        String tem;
                        String tem2="La carrera ha terminado. Distancia recorrida %.2f kilómetros en %d horas, %d minutos y %d segundos";
                        if (result.get(TimeUnit.HOURS)<10) tem="0%d";
                        else tem="%d";
                        if (result.get(TimeUnit.MINUTES)<10)tem+=":0%d";
                        else tem+=":%d";
                        if (result.get(TimeUnit.SECONDS)<10)tem+=":0%d";
                        else tem+=":%d";

                        String tempo= String.format(tem,result.get(TimeUnit.HOURS),result.get(TimeUnit.MINUTES),result.get(TimeUnit.SECONDS));
                        String tempo1= String.format("La carrera ha terminado: Distancia recorrida %.2f km, Tiempo:", dist/1000);
                        String tempo2= String.format(tem2,dist/1000,result.get(TimeUnit.HOURS),result.get(TimeUnit.MINUTES),result.get(TimeUnit.SECONDS));

                        //Toast.makeText(this, tempo1+tempo, Toast.LENGTH_LONG).show();
                        dist=0;

                        if (resultt==TextToSpeech.LANG_MISSING_DATA||resultt==TextToSpeech.LANG_NOT_SUPPORTED) Toast.makeText(getApplicationContext(),"TTS no soportado", Toast.LENGTH_SHORT).show();
                        else toSpeech.speak(tempo2,TextToSpeech.QUEUE_FLUSH,null);

                        comenzar=false;
                        index=0;
                    }
                }
                break;
        }
    }

    public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
        long diffInMilliSeconds = date2.getTime() - date1.getTime();
        List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);
        Map<TimeUnit, Long> result = new LinkedHashMap<TimeUnit, Long>();
        long milliSecondsRest = diffInMilliSeconds;
        for (TimeUnit unit : units) {
            long diff = unit.convert(milliSecondsRest, TimeUnit.MILLISECONDS);
            long diffInMilliSecondsForUnit = unit.toMillis(diff);
            milliSecondsRest = milliSecondsRest - diffInMilliSecondsForUnit;
            result.put(unit, diff);
        }
        return result;
    }

    public  static  float[] funcion_sonido_controlador(float val_actual,float val_ref, float hi, float hd)
    {
        float r;
        float l;
        float error=val_actual-val_ref;
        if (error>180) error-=360;
        else if (error<-180) error+=360;

        if(error>=0){
            l =  0.0f;
            if(error>=hd){
                r =  error;
                if(r<=45) {
                    if (getAuriculares_oseos()) r= (float) (Math.pow(1/31.0,-r/45.0)+69)/100;
                    else r= (float)(Math.pow(1/101.0,-r/45.0)-1)/100;
                }
                else r=100;
            }
            else r =0.0f;

        }
        else {
            r =  0.0f;
            if (error<=hi){
                l =  -error;
                if(l<=45) {
                    if (getAuriculares_oseos()) l= (float) (Math.pow(1/31.0,-l/45.0)+69)/100;
                    else l= (float)(Math.pow(1/101.0,-l/45.0)-1)/100;
                }
                else l=100;
            }
            else l=0.0f;
        }
        return new float[] {l,r};
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toSpeech!=null)
        {
            toSpeech.stop();
            toSpeech.shutdown();
        }

        mp.stop();
    }

    public int near(List<TrackPointsActivity> items, Location locationnow){

        int minIndex = -1;
        float minDist = 10000000; // initialize with a huge value that will be overwritten
        Location targetLocation = new Location("");//provider name is unnecessary

        for (int i = 0; i < items.size(); i++) {
            targetLocation.setLatitude(items.get(i).getitemLatitud());//your coords of course
            targetLocation.setLongitude(items.get(i).getitemLongitud());

            float distanceInMeters =  locationnow.distanceTo(targetLocation);
            if (distanceInMeters < minDist) {
                minDist = distanceInMeters;  // update neares
                minIndex = i;           // store index of nearest marker in minIndex
            }
        }
        //if(minIndex==(items.size()-1)) minIndex=-1;
        return minIndex;  //si se le aumenta 1 se asegura q sea un punto adelante de la persona
    }
}
/* Filtro de medio movil
int aux_inic=0;
    int ind_filtro=3;
    float[] vect_bearing=new float[3];
    boolean inici_bearing=true;

    calculo_distancia(location);
            vect_bearing[aux_inic]=location.getBearing();
            aux_inic++;
            if (aux_inic>=ind_filtro) {
                aux_inic = 0;
                if (inici_bearing = true) inici_bearing = false;
            }

            if(inici_bearing=false){
                float sum_bearing=0;
                for (int j=0;j>=(ind_filtro-1);j++){
                    sum_bearing+=vect_bearing[j];
                }
                bearing_actual=sum_bearing/ind_filtro;
            }
            tvPres.setText(String.format(" %d",aux_inic));*/
