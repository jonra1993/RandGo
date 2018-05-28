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
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener, GPXListeners.GPXParserListener, GPXListeners.GPXParserProgressListener  {

    int on=0;
    int branch=0;
    boolean comenzar=false;
    private Timer myTimer;
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
    long millis_before, millis_after, millis, elapsed;
    Date startDateTime, endDateTime;
    TextToSpeech toSpeech;
    int resultt;
    float bearing=500;

    MediaPlayer mp;
    float volumen_normal;
    PID pid;
    static Location lo=null;
    static Location dest=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //handle permissions first, before map is created. not depicted here
        DataHolder.setData("NULL");
        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission(this);
        }

        SharedPreferences prefs = getSharedPreferences("OSMNAVIGATOR", MODE_PRIVATE);

        //conectar layout
        btnMyLocation = findViewById(R.id.btnMyLocation);
        btnMyLocation.setOnClickListener(this);
        btnNavigation=findViewById(R.id.btnNavigation);
        btnNavigation.setOnClickListener(this);
        btnNavigation.hide();

        //inicialización del PID
        pid = new PID(1,0,0);
        pid.setOutputLimits(-100,100);
        //pid.setSetpoint((double)lo.bearingTo(dest));
        pid.setSetpoint(0);
        pid.setOutputFilter(0.1);
        volumen_normal=0.2f;
        mp=MediaPlayer.create(this,R.raw.exercise);
        mp.setLooping(true);


        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getOverlayManager().getTilesOverlay().setColorFilter(null);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setMaxZoomLevel(20.0);

        mapController = map.getController();
        mapController.setZoom(19.0);
        //GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        //mapController.setCenter(startPoint);

        //configuraciones de localización
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        //mLocationOverlay.getLastFix();
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

                switch (DataHolder.getData())
                {
                    case "GPX":
                        FilePicker();
                        DataHolder.setData("NULL");
                        break;
                    case "Carolina":
                        cargargpx("carolina.gpx");
                        DataHolder.setData("NULL");

                        break;
                    case "Estadiox":
                        cargargpx("casa-trabajo.gpx");
                        DataHolder.setData("NULL");

                        break;
                    case "Estadioy":
                        cargargpx("pista_carolina.gpx");
                        DataHolder.setData("NULL");
                        break;
                }
            }
        };
        myTimer.scheduleAtFixedRate(t,0,1000);

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                float copy=bearing;
                if(copy!=500&&comenzar==true)
                {
                    if(copy>180) copy=copy-360;
                    double ley=pid.getOutput((double) copy);
                    float error= (float) pid.getError();
                    Log.d("Ley de control", String.valueOf(ley));
                    float[] temp = funcion_sonido_pid((float) ley, volumen_normal * 100,error, -5,5);
                    Log.d("Volumen r", String.valueOf(temp[0]));
                    Log.d("Volumen l", String.valueOf(temp[1]));
                    mp.setVolume(temp[1], temp[0]);
                }

                else{
                    mp.setVolume((float)0.0, (float)0.0);
                }
            }
        };
        myTimer = new Timer();
        myTimer.scheduleAtFixedRate(tt,0,50);

        toSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.item1){
            if(comenzar==false){
                Intent intent = new Intent(MainActivity.this,OptionsActivity.class);
                startActivity(intent);
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
            //distanceInMeters += location.distanceTo(lastLocation);
            //lastLocation=location;
            bearing = location.getBearing();
        }
        else{
            bearing=500;
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
        Toast.makeText(getBaseContext(),locGeoPoint.getLatitude() + " - "+locGeoPoint.getLongitude(),Toast.LENGTH_SHORT).show();
        map.invalidate();
        if (location.hasBearing()==true) {
            direction=location.getBearing();
            Toast.makeText(getBaseContext(),""+direction+"o="+compassOverlay.getOrientation(),Toast.LENGTH_SHORT).show();
        } else {
                   //no se ha cambiado la posición
            Toast.makeText(getBaseContext(),"no tiene bearing"+"o="+compassOverlay.getOrientation(),Toast.LENGTH_SHORT).show();
        }
    }

    private void requestMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) this);
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

        List<GPXTrackPoint> mPoints = null;
        mPoints=document.getTracks().get(0).getSegments().get(0).getTrackPoints();
        ArrayList<GeoPoint> puntos = new ArrayList<GeoPoint>();
        for (int i = 0; i<(mPoints.size()); i++)
        {
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
        map.invalidate();
    }
    void setViewOn(BoundingBox bb){
        if (bb != null){
            map.zoomToBoundingBox(bb, true);
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
                        btnNavigation.show();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, "IOExeption opening file", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMyLocation:
                requestMyLocation();
                if(location!=null){
                    //Location lastFix=mLocationOverlay.getLastFix();
                    updateLocation(location);
                }

                break;

            case R.id.btnNavigation:
                if(comenzar==false){

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

                    Toast.makeText(this, "La carrera comienza Ahora", Toast.LENGTH_SHORT).show();
                    millis_before = System.currentTimeMillis();
                    startDateTime = new Date(millis_before);
                    mp.start();
                    comenzar=true;

                }
                else{
                    mp.pause();
                    millis_after = System.currentTimeMillis();
                    endDateTime = new Date(millis_after);
                    Map<TimeUnit,Long> result = computeDiff(startDateTime, endDateTime);
                    String tem;
                    String tem2="La carrera ha terminado. Distancia recorrida %d kilómetros en %d horas, %d minutos y %d segundos";
                    if (result.get(TimeUnit.HOURS)<10) tem="0%d";
                    else tem="%d";
                    if (result.get(TimeUnit.MINUTES)<10)tem+=":0%d";
                    else tem+=":%d";
                    if (result.get(TimeUnit.SECONDS)<10)tem+=":0%d";
                    else tem+=":%d";

                    String tempo= String.format(tem,result.get(TimeUnit.HOURS),result.get(TimeUnit.MINUTES),result.get(TimeUnit.SECONDS));
                    String tempo2= String.format(tem2,10,result.get(TimeUnit.HOURS),result.get(TimeUnit.MINUTES),result.get(TimeUnit.SECONDS));

                    Toast.makeText(this, "La carrera ha terminado: Distancia recorrida xxxx, tiempo:"+tempo, Toast.LENGTH_LONG).show();

                    if (resultt==TextToSpeech.LANG_MISSING_DATA||resultt==TextToSpeech.LANG_NOT_SUPPORTED) Toast.makeText(getApplicationContext(),"TTS no soportado", Toast.LENGTH_SHORT).show();
                    else toSpeech.speak(tempo2,TextToSpeech.QUEUE_FLUSH,null);

                    comenzar=false;
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

    public  static  float[] funcion_sonido_pid(float ley, float maxV,float error, float hi, float hd)
    {
        float r;
        float l;
        if (error<hi||error>hd)
        {
            r = maxV - ley;
            if (r >= 100) r = 100;
            else if (r < 0) r = 0;

            l = maxV + ley;
            if (l >= 100) l = 100;
            else if (l < 0) l = 0;
        }
        else
        {
            r=maxV;
            l=maxV;
        }

        r= (float) (r/100.0);
        l= (float) (l/100.0);

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
}
