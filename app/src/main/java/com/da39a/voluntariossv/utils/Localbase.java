package com.da39a.voluntariossv.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.da39a.voluntariossv.modelos.Usuario;

public class Localbase {

    SharedPreferences sp;

    public Localbase(Context ctx){
        sp = ctx.getSharedPreferences("VOLUNTARIOSSV_LOCALBASE",ctx.MODE_PRIVATE);
    }

    public void setUsuario(Usuario usuario){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("usuario_id",usuario.getId());
        editor.putString("usuario_tipo",usuario.getTipo());
        editor.commit();
    }

    public Usuario getUsuario(){
        Usuario usuario = new Usuario();
        usuario.setId(sp.getString("usuario_id",""));
        usuario.setTipo(sp.getString("usuario_tipo","voluntario"));
        return usuario;
    }

}
