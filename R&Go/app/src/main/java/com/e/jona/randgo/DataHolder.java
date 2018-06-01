package com.e.jona.randgo;


//https://stackoverflow.com/questions/4878159/whats-the-best-way-to-share-data-between-activities
//https://www.youtube.com/watch?v=ueeRgnYamO4&index=13&list=PLpOqH6AE0tNh5rvbCb03w8ORR8bOoftZ6

public class DataHolder {
    private static boolean audio;
    private static String orden;
    public static String getData() {
        return orden;
    }
    public static void setData(String data) {
        DataHolder.orden = data;
    }
    public static boolean getData_Audio() {
        return audio;
    }
    public static void setData_Audio(boolean data) { DataHolder.audio = data; }
}
