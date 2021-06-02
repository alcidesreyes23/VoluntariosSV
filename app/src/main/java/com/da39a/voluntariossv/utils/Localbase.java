package com.da39a.voluntariossv.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.da39a.voluntariossv.modelos.Institucion;
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

    public void setInstitucion(Institucion ins){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("institucion_id",ins.getId());
        editor.putString("institucion_descripcion",ins.getDescripcion());
        editor.putString("institucion_nombre",ins.getNombre());
        editor.putString("institucion_rubro",ins.getRubro());
        editor.putString("institucion_telefono",ins.getTelefono());
        editor.putString("institucion_latitud",ins.getLatitud()+"");
        editor.putString("institucion_longitud",ins.getLongitud()+"");
        editor.commit();
    }

    public Institucion getInstitucion(){
        Institucion institucion = new Institucion();
        institucion.setId(sp.getString("institucion_id",""));
        institucion.setDescripcion(sp.getString("institucion_descripcion",""));
        institucion.setNombre(sp.getString("institucion_nombre",""));
        institucion.setRubro(sp.getString("institucion_rubro",""));
        institucion.setTelefono(sp.getString("institucion_telefono",""));
        institucion.setLatitud(Double.parseDouble(sp.getString("institucion_latitud","")));
        institucion.setLongitud(Double.parseDouble(sp.getString("institucion_longitud","")));
        return institucion;
    }



}
