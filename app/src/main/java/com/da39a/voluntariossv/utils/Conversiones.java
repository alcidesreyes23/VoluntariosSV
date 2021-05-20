package com.da39a.voluntariossv.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Conversiones {

    public static String milisToDateString(long milis){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milis);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        return day + "/" + month + "/" + year + " - " + hour + ":" + min;
    }

    public static String metrosToDistanciaLabel(double mts){
        String distancia = "";
        if(mts >= 1000){
            double km = mts / 1000;
            double round = Math.round(km * 100.0) / 100.0;
            distancia = round + " kms";
        }else{
            double round = Math.round(mts * 100.0) / 100.0;
            distancia = round + " mts";
        }
        return distancia;
    }

}
