package com.da39a.voluntariossv.ui.configuraciones;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.da39a.voluntariossv.DatePickerFragment;
import com.da39a.voluntariossv.Login;
import com.da39a.voluntariossv.R;
import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.utils.Calculos;
import com.da39a.voluntariossv.utils.Constantes;
import com.da39a.voluntariossv.utils.Conversiones;
import com.da39a.voluntariossv.utils.CustomAlerts;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class ConfiguracionesFragment extends Fragment {

    //region DECLARACION DE VARIABLES
    Context ctx;
    Button btn_signout,btnActualizar;
    EditText edtTelefono, edtFecha, edtNombre, edtOcupacion, edtSuf;
    ImageButton btnCalendar;
    RadioButton rM, rF;
    CustomAlerts alerts;
    long fechaLong;
    ImageView perfil;
    static  final int  GALLERY_INTENT = 1;
    static  final int  COD_FOTO = 2;
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    Uri uriPerfil, photoUri;
    boolean estado;
    String genero;
    String mCurrentPhotoPath;
    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_configuraciones, container, false);
        ctx = (this.getContext());
        init(root);
        btn_signout = root.findViewById(R.id.btn_signout);
        btn_signout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            ConfiguracionesFragment.this.startActivity(new Intent(ctx, Login.class));
            //13.671402, -89.292708
            LatLng puntoA = new LatLng(13.670081,-89.293372);
            LatLng puntoB = new LatLng(13.671402,-89.292708);
            Log.e("DISTANCIA","MTS: " + Calculos.getDistancia(puntoA,puntoB));
        });
        return root;
    }

    //region PERDIR Y OBTENER PERMISOS DE ACCESO A LA CAMARA
    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions(   (Activity) context, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context, final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,(dialog, which) -> ActivityCompat.requestPermissions((Activity) context,new String[] { permission },MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE));
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
            else Toast.makeText(ctx, "GET_ACCOUNTS Denied", Toast.LENGTH_SHORT).show();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //endregion

    //region METODO INIT
    public  void  init(View v){
        //EditText
        edtTelefono = v.findViewById(R.id.telefono);
        edtFecha = v.findViewById(R.id.direccion);
        edtNombre = v.findViewById(R.id.nombre);
        edtOcupacion = v.findViewById(R.id.ocupacion);
        edtSuf = v.findViewById(R.id.sufijo);
        //Buttons
        btnActualizar = v.findViewById(R.id.btnRegistrar);
        btnCalendar  = v.findViewById(R.id.btnCal);
        //ImageView
        perfil = v.findViewById(R.id.imvPerfil);
        //RadioGroup
        rM = v.findViewById(R.id.radio_m);
        rF = v.findViewById(R.id.radio_f);
        //Alerts
        alerts =  new CustomAlerts(v.getContext());
        //Eventos Click
        btnCalendar.setOnClickListener(view -> showDatePickerDialog());
        btnActualizar.setOnClickListener(view -> Actualizar());
        perfil.setOnClickListener(v1 -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Escoga el metodo para obtener la foto de perfil.");
            String[] items = {"Galeria","Camara"};
            dialog.setItems(items, (dialog12, which) -> {
                switch (which){
                    case 0:
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, GALLERY_INTENT);
                        break;
                    case 1:
                        if (checkPermissionREAD_EXTERNAL_STORAGE(ctx)) abrirCamera();
                        break;
                }
            });
            AlertDialog dialog1 = dialog.create();
            dialog1.show();
        });
        //Cargar datos de perfil
        LoadUser();
    }
    //endregion

    //region METODOS PARA OBTNER IMAGEN DESDE GALERIA O TOMAR FOTO
    //Metodo para abrir camara
    private void abrirCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ignored) { }
            if (photoFile != null) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "MyPicture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
                photoUri = requireActivity().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, COD_FOTO);
            }
        }
    }

    //Crea la imagen
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName,".jpg",  storageDir );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    //Para obtener la url de la imagen desde la galeria
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case GALLERY_INTENT:
                if (resultCode == Activity.RESULT_OK && data != null){
                    assert data != null;
                    uriPerfil = data.getData();
                    estado = true;
                    perfil.setImageURI(uriPerfil);
                }
                break;
            case COD_FOTO:
                if (resultCode == Activity.RESULT_OK && data != null ){
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), photoUri);
                        perfil.setImageBitmap(bitmap);
                        uriPerfil =  photoUri;
                        estado = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    //Interface para obtener el url desde el Storage
    public  interface IDevolverURL{
        void urlFoto(String url);
    }
    //endregion

    //region METODOS PARA SUBIR LA IMAGEN
    //Subir Foto al storage
    public void  SubirFoto(Uri uri, IDevolverURL iDevolverURL){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //Crea una carpeta de fotos de perfil para cada usuario
        StorageReference dbR = storage.getReference("Fotos_perfil/"+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        //Agregar un nombre a la foto
        String nombreFoto;
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS.ss-mm-hh-dd-MM-yyyy", Locale.getDefault());
        nombreFoto = simpleDateFormat.format(date);
        //Crea el hijo de la foto
        final StorageReference fotoReferencia = dbR.child(nombreFoto);
        //Guarda la foto
        fotoReferencia.putFile(uri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            // continuar con el task para obtener la url de descarga
            return fotoReferencia.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String downloadURL = downloadUri.toString();
                iDevolverURL.urlFoto(downloadURL);
            }
        });
    }
    //endregion

    //region  ACTUALIZAR LOS DATOS DE PERFIL
    //Metodo que acutaliza
    public  void Actualizar () {
        String tel = edtTelefono.getText().toString();
        String suf = edtSuf.getText().toString();
        // String fecha =  edtFecha.getText().toString();
        String nombre = edtNombre.getText().toString();
        String ocupacion = edtOcupacion.getText().toString();

        if (rM.isChecked()) genero = "Masculino";
        else if (rF.isChecked()) genero = "Femenino";

        Map<String,Object> datos = new HashMap<>();
        datos.put("nombre",nombre);
        datos.put("ocupacion",ocupacion);
        datos.put("nacimiento",fechaLong);
        datos.put("sexo",genero);
        datos.put("telefono",null);

        Map<String, Object> dataTel = new HashMap<>();
        dataTel.put("numero",tel);
        dataTel.put("sufijo",suf);

        //Valida que se haya seleccionado una foto
        if (estado) {
            //Si se ha selecionado la sube y guarda los datos
            SubirFoto(uriPerfil, url -> {
                datos.put("fotoPerfil", url);
                EnviarDatos(datos,dataTel);
            });
            estado = false;
        } else {
            //Sino se ha seleccionado y no tiene foto de antes coloca la de por defecto
            if (uriPerfil.toString().equals(Constantes.FOTO_POR_DEFECTO_F) || uriPerfil.toString().equals(Constantes.FOTO_POR_DEFECTO_M) ) {
                if (genero.equals("Masculino"))  datos.put("fotoPerfil", Constantes.FOTO_POR_DEFECTO_M);
                else  datos.put("fotoPerfil", Constantes.FOTO_POR_DEFECTO_F);
            }
            else  datos.put("fotoPerfil", uriPerfil.toString());

            EnviarDatos(datos,dataTel);
        }
    }

    //Funciona para enviar los datos actualizado a la base
    public void EnviarDatos(Map<String,Object> datos, Map<String,Object> dataTel){
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        new Realtimedb().getVoluntario(uid).updateChildren(datos).addOnSuccessListener(aVoid ->
                new Realtimedb().getVoluntario(uid).child("telefono").updateChildren(dataTel).addOnSuccessListener(aVoid1 -> {
                    alerts.setType(CustomAlerts.MODALTYPE.SUCCESS);
                    alerts.setTitle("HECHO");
                    alerts.setMensage("Los datos han sido actualizados.");
                    alerts.setEstado(false);
                    alerts.show();
                }).addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthException){
                        alerts.setType(CustomAlerts.MODALTYPE.DANGER);
                        alerts.setTitle("Error al registrar cuenta");
                        alerts.setMensage(e.getMessage());
                        alerts.show();
                    }
                })).addOnFailureListener(e -> {
            if (e instanceof FirebaseAuthException){
                alerts.setType(CustomAlerts.MODALTYPE.DANGER);
                alerts.setTitle("Error al registrar cuenta");
                alerts.setMensage(e.getMessage());
                alerts.show();
            }
        });
    }
    //endregion

    //region OBTENER LOS DATOS ACTUALIZADOS
    //Carga los datos del perfil
    public void LoadUser () {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        new Realtimedb().getVoluntarios().child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isAdded()) {
                    if (snapshot.exists()) {
                        //Validacion por s no existe foto
                        if (snapshot.child("fotoPerfil").exists()) {
                            String url = Objects.requireNonNull(snapshot.child("fotoPerfil").getValue()).toString();
                            //Guarda el url de la foto ya guardada
                            uriPerfil = Uri.parse(Objects.requireNonNull(snapshot.child("fotoPerfil").getValue()).toString());
                            Glide.with(ConfiguracionesFragment.this).load(url).dontAnimate().fitCenter().centerCrop().into(perfil);
                        }
                        long datoF = Long.parseLong(Objects.requireNonNull(snapshot.child("nacimiento").getValue()).toString());
                        String date = Conversiones.milisToDateString(datoF);
                        String[] fecha = date.split(" ");
                        fechaLong = datoF;
                        edtNombre.setText(Objects.requireNonNull(snapshot.child("nombre").getValue()).toString());
                        edtFecha.setText(fecha[0]);
                        edtOcupacion.setText(Objects.requireNonNull(snapshot.child("ocupacion").getValue()).toString());
                        if (snapshot.child("sexo").exists()) {
                            if (snapshot.child("sexo").getValue().toString().equals("Masculino"))
                                rM.setChecked(true);
                            else
                                rF.setChecked(true);
                        }
                        //Validacion por si no existe foto
                        if (snapshot.child("telefono").exists()) {
                            edtSuf.setText(Objects.requireNonNull(snapshot.child("telefono").child("sufijo").getValue()).toString());
                            edtTelefono.setText(Objects.requireNonNull(snapshot.child("telefono").child("numero").getValue()).toString());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // endregion

    //region OBTENER FECHA
    //Seleccionar fecha
    public void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
            fechaLong = new GregorianCalendar(year,month,day).getTimeInMillis();
            edtFecha.setText(selectedDate);
        });
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
    }

    public String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }
    //endregion
}
