package com.da39a.voluntariossv.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.da39a.voluntariossv.R;
import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.firelisteners.Checklogin;

public class CustomAlerts{

    public enum MODALTYPE { SUCCESS, DANGER, WARNING, INFO }
    private String title;
    private String message;
    private MODALTYPE type;
    private String uid;
    private  boolean estado;
    Context ctx;

    public CustomAlerts(Context c){
        this.ctx = c;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMensage(String mensage) {
        this.message = mensage;
    }

    public void setType(MODALTYPE type) {
        this.type = type;
    }

    public void setUid(String uid) { this.uid = uid; }

    public void setEstado(boolean estado) { this.estado = estado; }

    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.ctx);

        View v = LayoutInflater.from(ctx).inflate(R.layout.view_modal,null,false);
        RelativeLayout modal_header = v.findViewById(R.id.modal_header);
        ImageView modal_icon = v.findViewById(R.id.modal_icon);
        TextView modal_title = v.findViewById(R.id.modal_titulo);
        TextView modal_message = v.findViewById(R.id.modal_menssage);
        modal_message.setText("");

        switch (this.type){
            case SUCCESS:
                modal_header.setBackgroundColor(ctx.getResources().getColor(R.color.color_success));
                modal_icon.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_check));
                modal_title.setText("Realizado!");
                break;
            case DANGER:
                modal_header.setBackgroundColor(ctx.getResources().getColor(R.color.color_danger));
                modal_icon.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_danger));
                modal_title.setText("Alerta!");
                break;
            case WARNING:
                modal_header.setBackgroundColor(ctx.getResources().getColor(R.color.color_warning));
                modal_icon.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_error_circle));
                modal_title.setText("Advertencia!");
                break;
            case INFO:
                modal_header.setBackgroundColor(ctx.getResources().getColor(R.color.color_info));
                modal_icon.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_info_msg));
                modal_title.setText("Informacion!");
                break;
        }

        if(this.title != null){
            modal_title.setText(this.title);
        }

        if(this.message != null){
            modal_message.setText(this.message);
        }

        if (estado){
            builder.setPositiveButton("Ok", (dialog, which) -> {
                new Realtimedb().getUsuario(uid).addListenerForSingleValueEvent(new Checklogin(ctx));
            });
        } else {
            builder.setPositiveButton("Ok",null);
        }
        builder.setView(v);
        builder.setCancelable(true);
        builder.create().show();

    }

}
