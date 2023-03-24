package com.example.plantea.presentacion.actividades;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantea.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ConfiguracionActivity extends AppCompatActivity {

    private ImageView img_usuarioPlanificador, img_usuarioTEA, img_objeto;
    private TextView txt_Planificador, txt_UsuarioTEA, txt_objeto;
    private Button btn_guardar;
    private Switch btn_notificacion, lbl_infoUsuario;
    private CheckBox semana, dia, hora;

    private boolean es_planificador = false, es_objeto = false, notificacion_activa = false, info_usuario = false;

    //Variables codigo de permiso y acceso a galeria
    private static final int REQUEST_CODIGO_PERMISO = 100;
    private static final int REQUEST_GALERIA = 101;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configuracion);
        // int orientation = getResources().getConfiguration().orientation;
        // if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        //     setContentView(R.layout.activity_configuracion);
        // } else {
        //     setContentView(R.layout.activity_configuracion_portrait);
        // }

        //Activamos icono volver atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img_usuarioPlanificador = findViewById(R.id.img_FotoPlanificador);
        img_usuarioTEA = findViewById(R.id.img_FotoUsuarioTEA);
        img_objeto = findViewById(R.id.img_objeto);
        txt_Planificador = findViewById(R.id.txt_nombrePlanificador);
        txt_UsuarioTEA = findViewById(R.id.txt_nombreUsuarioTEA);
        txt_objeto = findViewById(R.id.txt_nombreObjeto);
        btn_guardar = findViewById(R.id.btn_guardarConfiguracion);
        btn_notificacion = findViewById(R.id.switch_notificacion);
        lbl_infoUsuario = findViewById(R.id.lbl_infoUsuarioTEA);
        semana = findViewById(R.id.checkBox_semana);
        dia = findViewById(R.id.checkBox_dia);
        hora = findViewById(R.id.checkBox_hora);

        txt_UsuarioTEA.setEnabled(false);
        img_usuarioTEA.setEnabled(false);
        lbl_infoUsuario.setChecked(false);

        //Preferencias
        SharedPreferences prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);

        //Recuperamos la información cuando no es la primera vez de acceso.
        Boolean accesoConfiguracion = prefs.getBoolean("configuracion", false);
        if(accesoConfiguracion){
            //Imagenes y nombres
            txt_Planificador.setText(prefs.getString("nombrePlanificador","").toUpperCase());
            txt_UsuarioTEA.setText(prefs.getString("nombreUsuarioTEA","").toUpperCase());
            txt_objeto.setText(prefs.getString("nombreObjeto","").toUpperCase());

            img_objeto.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128);

            if (prefs.getString("imagenPlanificador","") == ""){
                img_usuarioPlanificador.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128);
            }
            else{
                img_usuarioPlanificador.setBackground(null);
                img_usuarioPlanificador.setImageURI(Uri.parse(prefs.getString("imagenPlanificador","")));
            }

            if (prefs.getString("imagenUsuarioTEA","") == ""){
                img_usuarioTEA.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128);
            }
            else{
                img_usuarioTEA.setBackground(null);
                img_usuarioTEA.setImageURI(Uri.parse(prefs.getString("imagenUsuarioTEA","")));
            }

            if (prefs.getString("imagenObjeto","") == ""){
                img_objeto.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128);
            }
            else{
                img_objeto.setBackground(null);
                img_objeto.setImageURI(Uri.parse(prefs.getString("imagenObjeto","")));
            }

            //Notificaciones
            notificacion_activa = prefs.getBoolean("notificaciones", false);
            semana.setChecked(prefs.getBoolean("notificacion_semana", false));
            dia.setChecked(prefs.getBoolean("notificacion_dia", false));
            hora.setChecked(prefs.getBoolean("notificacion_hora", false));
            if(notificacion_activa){
                btn_notificacion.setChecked(true);
                semana.setEnabled(true);
                dia.setEnabled(true);
                hora.setEnabled(true);
            }else{
                btn_notificacion.setChecked(false);
                semana.setEnabled(false);
                dia.setEnabled(false);
                hora.setEnabled(false);
            }

            info_usuario = prefs.getBoolean("info_usuario", false);

            if(info_usuario){
                lbl_infoUsuario.setChecked(true);
                txt_UsuarioTEA.setEnabled(true);
                img_usuarioTEA.setEnabled(true);
            }else{
                lbl_infoUsuario.setChecked(false);
                txt_UsuarioTEA.setEnabled(false);
                img_usuarioTEA.setEnabled(false);
            }


        }

        img_usuarioPlanificador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarPermisos();
                es_planificador = true;
                es_objeto = false;
            }
        });

        img_usuarioTEA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarPermisos();
                es_planificador = false;
                es_objeto = false;
            }
        });

        img_objeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarPermisos();
                es_objeto = true;
                es_planificador = false;
            }
        });

        btn_notificacion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //ON
                    semana.setChecked(true);
                    semana.setEnabled(true);
                    dia.setEnabled(true);
                    hora.setEnabled(true);
                }else{
                    //OFF
                    semana.setEnabled(false);
                    dia.setEnabled(false);
                    hora.setEnabled(false);
                }
            }
        });

        
        lbl_infoUsuario.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //ON
                    txt_UsuarioTEA.setEnabled(true);
                    img_usuarioTEA.setEnabled(true);

                }else{
                    //OFF
                    txt_UsuarioTEA.setEnabled(false);
                    img_usuarioTEA.setEnabled(false);
                }
            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txt_Planificador.getText().toString().isEmpty() || (txt_UsuarioTEA.getText().toString().isEmpty()) && lbl_infoUsuario.isChecked()){
                    Toast.makeText(getApplicationContext(), "Se necesita un nombre para cada usuario", Toast.LENGTH_LONG).show();
                }else if(img_usuarioPlanificador.getDrawable() == null || (img_usuarioTEA.getDrawable() == null && lbl_infoUsuario.isChecked() ) ){
                    Toast.makeText(getApplicationContext(), "Se necesita una imagen para cada usuario", Toast.LENGTH_LONG).show();
                }else if(txt_objeto.getText().toString().isEmpty() || img_objeto.getDrawable() == null){
                    Toast.makeText(getApplicationContext(), "Se necesita una imagen y nombre del objeto tranquilizador", Toast.LENGTH_LONG).show();
                }else{
                    //Obtener nombres de los usuarios y objeto
                    String nombreUsuarioPlanificador= txt_Planificador.getText().toString();
                    String nombreUsuarioTEA= txt_UsuarioTEA.getText().toString();
                    String nombreObjeto = txt_objeto.getText().toString();
                    String rutaUsuarioTEA = "";

                    String rutaPlanificador = crearRuta(img_usuarioPlanificador,"Planificador");
                    if(lbl_infoUsuario.isChecked()){
                        rutaUsuarioTEA = crearRuta(img_usuarioTEA,"Usuario");
                    }
                    String rutaObjeto = crearRuta(img_objeto, "Objeto");

                    if(accesoConfiguracion){
                        finish();
                    }else{
                        //Abre la pantalla inicio porque es la primera vez
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }

                    //Cambiamos el valor en preferencias para no acceder a configuracion en el siguiente inicio y guardamos datos de los usuarios
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("configuracion", true);
                    editor.putBoolean("notificaciones", btn_notificacion.isChecked());
                    editor.putBoolean("notificacion_semana", semana.isChecked());
                    editor.putBoolean("notificacion_dia", dia.isChecked());
                    editor.putBoolean("notificacion_hora", hora.isChecked());
                    editor.putString("nombrePlanificador", nombreUsuarioPlanificador);
                    editor.putString("nombreUsuarioTEA", nombreUsuarioTEA);
                    editor.putString("nombreObjeto", nombreObjeto);
                    editor.putString("imagenPlanificador", rutaPlanificador);
                    editor.putString("imagenUsuarioTEA", rutaUsuarioTEA);
                    editor.putString("imagenObjeto", rutaObjeto);
                    editor.putBoolean("info_usuario", lbl_infoUsuario.isChecked());


                    editor.commit();
                }
            }
        });
    }
    //Menu principal
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ayuda, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.item_ayuda_menu:
                Intent i= new Intent(getApplicationContext(), ManualActivity.class);
                startActivity(i);
                break;
        }
        return true;
    }

    private String crearRuta(ImageView imagen, String nombreImagen){
        Bitmap image = ((BitmapDrawable)imagen.getDrawable()).getBitmap();

        //Escalar imagen
        float proporcion = 500 / (float) image.getWidth();
        Bitmap imagenFinal = Bitmap.createScaledBitmap(image,500,(int) (image.getHeight() * proporcion),false);

        //Guardar imagen
        String ruta = guardarImagen(getApplicationContext(), nombreImagen, imagenFinal);

        return ruta;
    }

    private String guardarImagen (Context context, String nombre, Bitmap imagen){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Imagenes", Context.MODE_PRIVATE);
        File myPath = new File(dirImages, nombre + ".png");

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(myPath);
            imagen.compress(Bitmap.CompressFormat.PNG, 10, fos); // calidad a 0 imagen mas pequeña
            fos.flush();
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return myPath.getAbsolutePath();
    }

    private void comprobarPermisos(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(ConfiguracionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                abrirGaleria();
            }else{
                ActivityCompat.requestPermissions(ConfiguracionActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODIGO_PERMISO);
            }
        }else {
            abrirGaleria();
        }
    }

    private void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent, REQUEST_GALERIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_GALERIA){
            if(resultCode == Activity.RESULT_OK){
                Uri image = data.getData();
                if(es_planificador){
                    img_usuarioPlanificador.setBackground(null);
                    img_usuarioPlanificador.setImageURI(image);
                }else if (es_objeto){
                    img_objeto.setBackground(null);
                    img_objeto.setImageURI(image);
                }else{
                    img_usuarioTEA.setBackground(null);
                    img_usuarioTEA.setImageURI(image);
                }
            }else{
                Toast.makeText(this, "No se ha seleccionado una imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODIGO_PERMISO) {
            if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                abrirGaleria();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}