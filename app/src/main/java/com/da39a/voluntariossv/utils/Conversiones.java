package com.da39a.voluntariossv.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.da39a.voluntariossv.R;

import java.time.DayOfWeek;
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

    public static String milisToLargeDateString(long milis){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milis);
        String builder = "";

        String[] meses = new String[]{"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Nomviembre","Diciembre"};
        String[] dias = new String[]{"Domingo","Lunes","Martes","Miercoles","Jueves","Viernes","Sabado"};

        builder += dias[calendar.get(Calendar.DAY_OF_WEEK)-1] + " " + calendar.get(Calendar.DAY_OF_MONTH) + " de ";
        builder += meses[(calendar.get(Calendar.MONTH))] + " del ";
        builder += calendar.get(Calendar.YEAR);

        return builder;
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

    public static String UriRealPath(Context ctx, Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = ctx.getContentResolver().query(uri, proj, null, null, null);
            int col = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(col);
        } catch (Exception e) {

        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }


    public static int getRubroImg(String rubro){
        switch (rubro){
            case "Educación":
                return R.drawable.img_educacion;
            case "Turismo":
                return R.drawable.img_turismo;
            case "Software":
                return R.drawable.img_software;
            case "Salud":
                return R.drawable.img_salud;
            case "Ganadería":
                return R.drawable.img_ganaderia;
            case "Pesca":
                return R.drawable.img_pesca;
            case "Construcción":
                return R.drawable.img_construccion;
            case "Telecomunicaciones":
                return R.drawable.img_telecomunicaciones;
            case "Editorial":
                return R.drawable.img_editorial;
            case "Mercadotecnia":
                return R.drawable.img_mercadotecnia;
            case "Otra":
                return R.drawable.img_landscape;
        }
        return 0;
    }

}
