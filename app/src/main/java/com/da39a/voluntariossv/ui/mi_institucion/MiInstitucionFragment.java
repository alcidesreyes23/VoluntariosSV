package com.da39a.voluntariossv.ui.mi_institucion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.da39a.voluntariossv.PlacePicker;
import com.da39a.voluntariossv.R;
import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.modelos.Institucion;
import com.da39a.voluntariossv.utils.Constantes;
import com.da39a.voluntariossv.utils.Conversiones;
import com.da39a.voluntariossv.utils.CustomAlerts;
import com.da39a.voluntariossv.utils.Localbase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class MiInstitucionFragment extends Fragment {

    public final int ACTIVITY_GALLERY = 100;
    public final int ACTIVITY_MAP = 200;

    EditText et_nombre,et_descripcion,et_telefono,et_ubicacion;
    ImageView institucion_img,btn_gallery,btn_map;
    Institucion miInstitucion;
    double latitud,longitud;
    Spinner spin_rubro;
    Button btn_guardar;
    Uri picuri = null;

    DatabaseReference ref;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mi_institucion, container, false);
        init(root);
        load();
        return root;
    }

    public void init(View v){
        //EditTexts
        et_nombre = v.findViewById(R.id.institucion_nombre);
        et_descripcion = v.findViewById(R.id.institucion_descripcion);
        et_telefono = v.findViewById(R.id.institucion_telefono);
        et_ubicacion = v.findViewById(R.id.institucion_direccion);

        //Imageviews
        institucion_img = v.findViewById(R.id.institucion_img);
        btn_gallery = v.findViewById(R.id.btn_gallery);
        btn_map = v.findViewById(R.id.btnMapPicker);
        btn_guardar = v.findViewById(R.id.btnGuardar);

        //Spinner
        spin_rubro = v.findViewById(R.id.institucion_rubro);

        miInstitucion = new Localbase(this.getContext()).getInstitucion();
        ref = new Realtimedb().getInstitucion(miInstitucion.getId());
        latitud = miInstitucion.getLatitud();
        longitud = miInstitucion.getLongitud();
    }

    public void load(){
        et_nombre.setText(miInstitucion.getNombre());
        et_descripcion.setText(miInstitucion.getDescripcion());
        et_telefono.setText(miInstitucion.getTelefono());
        et_ubicacion.setText(latitud + "," + longitud);

        btn_gallery.setOnClickListener(new OpenGallery());
        btn_map.setOnClickListener(new OpenMapPicker());
        btn_guardar.setOnClickListener(new SaveInstitucion());

        String[] rubros = Constantes.getRubros(this.getContext());
        for(int i = 0; i < rubros.length; i++){
            if(miInstitucion.getRubro().equals(rubros[i])){
                spin_rubro.setSelection(i);
            }
        }

    }

    public class OpenGallery implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent in = new Intent(Intent.ACTION_PICK);
            in.setType("image/*");
            String[] types = {"image/jpg","image/png","image/jpeg"};
            in.putExtra(Intent.EXTRA_MIME_TYPES,types);
            startActivityForResult(in,ACTIVITY_GALLERY);
        }
    }

    public class OpenMapPicker implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(MiInstitucionFragment.this.getContext(), PlacePicker.class), ACTIVITY_MAP);
        }
    }

    public class SaveInstitucion implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String msg = validar();
            if(msg.equals("ok")){
                Institucion updated = new Institucion();
                updated.setNombre(et_nombre.getText().toString());
                updated.setDescripcion(et_descripcion.getText().toString());
                updated.setTelefono(et_telefono.getText().toString());
                updated.setRubro(spin_rubro.getSelectedItem().toString());
                updated.setId(miInstitucion.getId());
                updated.setLatitud(latitud);
                updated.setLongitud(longitud);

                //GUARDAR EN LOCALBASE
                new Localbase(MiInstitucionFragment.this.getContext()).setInstitucion(updated);

                ref.setValue(updated.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("PICURI",picuri.getPath());
                        if(picuri != null){
                            uploadPicture();
                        }else{
                            datosEnviados();
                        }
                    }
                });
            }else{
                CustomAlerts ca = new CustomAlerts(MiInstitucionFragment.this.getContext());
                ca.setType(CustomAlerts.MODALTYPE.WARNING);
                ca.setTitle("Error! Datos Invalidos");
                ca.setMensage(msg);
                ca.show();
            }
        }
    }

    private void uploadPicture(){
        ProgressDialog pgd = new ProgressDialog(this.getContext());
        pgd.setTitle("Subiendo imagen");
        pgd.setCancelable(false);
        pgd.setCanceledOnTouchOutside(false);
        pgd.show();

        StorageReference stg = FirebaseStorage.getInstance().getReference().child("Instituciones").child(miInstitucion.getId()+".jpg");
        stg.putFile(picuri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                long progress = (snapshot.getBytesTransferred() / snapshot.getTotalByteCount()) * 100;
                pgd.setProgress((int) progress);
                if(progress == 100) pgd.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                picuri = null;
                datosEnviados();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CustomAlerts ca = new CustomAlerts(MiInstitucionFragment.this.getContext());
                ca.setTitle("Error!");
                ca.setMensage("No se pudo subir la imagen al servidor");
                ca.setType(CustomAlerts.MODALTYPE.DANGER);
                ca.show();
            }
        });
    }

    private void datosEnviados(){
        CustomAlerts ca = new CustomAlerts(MiInstitucionFragment.this.getContext());
        ca.setType(CustomAlerts.MODALTYPE.SUCCESS);
        ca.setTitle("Datos Actualizados!");
        ca.setMensage("Los nuevos datos han sido guardados exitosamente!");
        ca.show();
    }

    //ACTIVITY RESULT
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTIVITY_MAP){
            if(data != null) setUbicacion(data);
        }else if(requestCode == ACTIVITY_GALLERY){
            if(data != null) {
                picuri = data.getData();
                Glide.with(this.getContext()).load(data.getData()).into(institucion_img);
            }
        }
    }

    public void setUbicacion(Intent data){
        latitud = data.getExtras().getDouble("latitud");
        longitud = data.getExtras().getDouble("longitud");
        et_ubicacion.setText(latitud + "," + longitud);
    }

    //Validacion
    public String validar(){
        if(et_nombre.getText().toString().isEmpty()){
            return "El campo de nombre de la institución esta vacio";
        }

        if(et_descripcion.getText().toString().isEmpty()){
            return "El campo de descripción de la institución esta vacio";
        }

        if(et_telefono.getText().toString().isEmpty()){
            return "El campo de teléfono esta vacio";
        }

        if(spin_rubro.getSelectedItemPosition() == 0){
            return "No ha seleccionado un rubro de la institución";
        }

        if(et_ubicacion.getText().toString().isEmpty()){
            return "No ha seleccionado una ubicación de la institución";
        }

        return "ok";
    }

}