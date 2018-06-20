package com.e.jona.randgo;


//https://stackoverflow.com/questions/4878159/whats-the-best-way-to-share-data-between-activities
//https://www.youtube.com/watch?v=ueeRgnYamO4&index=13&list=PLpOqH6AE0tNh5rvbCb03w8ORR8bOoftZ6

public class DataHolder {
    private static boolean audio,aur_oseos;
    private static float P, I, D;
    private static String orden;

    public static String getData() {
        return orden;
    }

    public static void setData(String data) {
        DataHolder.orden = data;
    }

    public static boolean getAuriculares_oseos() {
        return aur_oseos;
    }

    public static void setAuriculares_oseos(boolean data) {
        DataHolder.aur_oseos = data;
    }

    public static boolean getData_Audio() {
        return audio;
    }

    public static void setData_Audio(boolean data) {
        DataHolder.audio = data;
    }

    public static float getPID_P() {
        return P;
    }

    public static float getPID_I() {
        return I;
    }

    public static float getPID_D() {
        return D;
    }

    public static void setPID(float P, float I, float D) {
        DataHolder.P = P;
        DataHolder.I = I;
        DataHolder.D = D;
    }
}
