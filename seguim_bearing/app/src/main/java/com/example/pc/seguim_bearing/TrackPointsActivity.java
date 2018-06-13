package com.example.pc.seguim_bearing;

public class TrackPointsActivity {
    private int itemNumber;
    private float itemLatitud;
    private float itemLongitud;
    private float itemBearing;
    private float itemBearing_ant;
    private float itemDistancia;

    public TrackPointsActivity(int num, float lat, float lon, float bearing, float dista, float bearing_ant) {
        this.itemNumber = num;
        this.itemLatitud = lat;
        this.itemLongitud =lon;
        this.itemBearing = bearing;
        this.itemDistancia=dista;
        this.itemBearing_ant=bearing_ant;
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

    public float getitemBearing_ant() {
        return this.itemBearing_ant;
    }

    public float getItemDistancia() {
        return this.itemDistancia;
    }

}