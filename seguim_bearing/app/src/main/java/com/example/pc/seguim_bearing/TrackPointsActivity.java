package com.example.pc.seguim_bearing;

public class TrackPointsActivity {
    private int itemNumber;
    private float itemLatitud;
    private float itemLongitud;
    private float itemBearing;

    public TrackPointsActivity(int num, float lat, float lon, float bearing) {
        this.itemNumber = num;
        this.itemLatitud = lat;
        this.itemLongitud =lon;
        this.itemBearing = bearing;
    }

    public int itemNumber() {
        return this.itemNumber;
    }

    public float itemLatitud() {
        return this.itemLatitud;
    }

    public float itemLongitud() {
        return this.itemLongitud;
    }

    public float itemBearing() {
        return this.itemBearing;
    }

}