package com.da39a.voluntariossv.utils;

import android.content.Context;

import com.da39a.voluntariossv.R;

import java.util.List;

public class Constantes {
    public  static final String FOTO_POR_DEFECTO = "https://firebasestorage.googleapis.com/v0/b/voluntariossv-e130e.appspot.com/o/Fotos_perfil%2Fperfil_defecto.png?alt=media&token=038294bd-a990-4616-b20b-f2cfc33c0cdc";
    public static final  String FOTO_POR_DEFECTO_M = "https://firebasestorage.googleapis.com/v0/b/voluntariossv-e130e.appspot.com/o/Fotos_perfil%2Fperfil_defecto_hombre.png?alt=media&token=2308c3ed-1ec4-4479-863f-3c1041f64ba0";
    public static final  String FOTO_POR_DEFECTO_F = "https://firebasestorage.googleapis.com/v0/b/voluntariossv-e130e.appspot.com/o/Fotos_perfil%2Fperfil_defecto_mujer.png?alt=media&token=ac30e0d7-eb88-4d65-9b6e-e62a42b7c0b4";

    public static String[] getRubros(Context ctx){
        return ctx.getResources().getStringArray(R.array.rubros);
    }

}
