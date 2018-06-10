package com.e.jona.randgo;

public class TrackPointsActivity {
    private int itemNumber;
    private float itemLatitud;
    private float itemLongitud;
    private float itemBearing;
    private float itemDistancia;

    public TrackPointsActivity(int num, float lat, float lon, float bearing, float dista) {
        this.itemNumber = num;
        this.itemLatitud = lat;
        this.itemLongitud =lon;
        this.itemBearing = bearing;
        this.itemDistancia=dista;
    }

    public int getitemNumber() {
        return this.itemNumber;
    }

    public float getitemLatitud() {
        return this.itemLatitud;
    }

    public float getitemLongitud() {
        return this.itemLongitud;
    }

    public float getitemBearing() {
        return this.itemBearing;
    }

    public float getItemDistancia() {
        return this.itemDistancia;
    }

}
