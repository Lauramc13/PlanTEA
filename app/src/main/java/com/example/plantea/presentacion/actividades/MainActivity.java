package com.example.plantea.presentacion.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.plantea.R;
import com.example.plantea.dominio.Planificacion;
import com.example.plantea.dominio.Usuario_Planificador;
import com.example.plantea.persistencia.ConectorBD;
import com.example.plantea.presentacion.actividades.ninio.PlanActivity;
import com.example.plantea.presentacion.actividades.planificador.CalendarioActivity;
import com.example.plantea.presentacion.actividades.MenuActivity;


public class MainActivity extends AppCompatActivity {

    private ImageView icono_cerrar, icono_cerrar_login, icono_ayuda;
    private ImageView image_Planificador;
    private ImageView image_UsuarioTEA;
    private ConectorBD conectorBD;
    private TextView password_nueva, password_repetida, password, nombrePlanificador, nombreUsuarioTEA;
    private Button btn_guardar;
    private Button btn_acceder;
    private CardView cardUsuarioTEA, cardUsuarioPlanificador;

    Usuario_Planificador usuario = new Usuario_Planificador();

    private boolean info_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conectorBD = new ConectorBD(this);
        conectorBD.abrir();
        conectorBD.cerrar();
        image_Planificador = findViewById(R.id.image_RolPlanificador);
        image_UsuarioTEA = findViewById(R.id.image_RolTEA);
        icono_ayuda = findViewById(R.id.image_Manual);
        nombrePlanificador = findViewById(R.id.lbl_nombrePlanificador);
        nombreUsuarioTEA = findViewById(R.id.lbl_nombreUsuarioTEA);
        cardUsuarioPlanificador = findViewById(R.id.cardViewPlanificador);
        cardUsuarioTEA = findViewById(R.id.cardViewUsuarioTEA);

        //Preferencias
        SharedPreferences prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);

        info_usuario = prefs.getBoolean("info_usuario", false);
        if(!info_usuario){
            cardUsuarioTEA.setVisibility(View.GONE);
        }


        String rutaUsuarioTEA = prefs.getString("imagenUsuarioTEA","");
        String rutaPlanificador = prefs.getString("imagenPlanificador","");
        nombrePlanificador.setText(prefs.getString("nombrePlanificador","").toUpperCase());
        nombreUsuarioTEA.setText(prefs.getString("nombreUsuarioTEA","").toUpperCase());
        image_UsuarioTEA.setImageURI(Uri.parse(rutaUsuarioTEA));
        image_Planificador.setImageURI(Uri.parse(rutaPlanificador));

        //Este método se ejecutará al pinchar sobre la imagen del rol planificador
        cardUsuarioPlanificador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean primerAcceso = prefs.getBoolean("password", false);
                if (primerAcceso){ //No es la primera vez que se lanza la app
                    crearDialogoLogin();
                } else {
                    crearDialogoPassword();
                }
            }
        });

        //Este método se ejecutará al pinchar sobre la imagen del rol niño
        cardUsuarioTEA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlanActivity.class);
                startActivity(intent);
            }
        });

        icono_ayuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManualActivity.class);
                startActivity(intent);
            }
        });
    }

    public void crearDialogoLogin(){
        Dialog dialogLogin = new Dialog(this);
        dialogLogin.setContentView(R.layout.dialogo_login);
        dialogLogin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        password = dialogLogin.findViewById(R.id.txt_Password);
        btn_acceder = dialogLogin.findViewById(R.id.btn_login);
        icono_cerrar_login = dialogLogin.findViewById(R.id.icono_CerrarDialogo);

        btn_acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Introduce la contraseña", Toast.LENGTH_LONG).show();
                }else{
                    boolean passCorrecta = usuario.comprobarPass(password.getText().toString(), MainActivity.this);
                    if(passCorrecta){
                        //if(!info_usuario){
                        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(intent);
                        dialogLogin.dismiss();
                        //}
                        // else{
                        //     Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                        //     startActivity(intent);
                        //     dialogLogin.dismiss();
                        // }

                    }else{
                        Toast.makeText(getApplicationContext(), "Error en la contraseña", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        icono_cerrar_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogLogin.dismiss();
            }
        });

        dialogLogin.show();
    }

    public void crearDialogoPassword(){
        Dialog dialogCrearPass = new Dialog(this);
        dialogCrearPass.setContentView(R.layout.dialogo_crear_password);
        dialogCrearPass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCrearPass.show();

        password_nueva = dialogCrearPass.findViewById(R.id.txt_NewPass);
        password_repetida = dialogCrearPass.findViewById(R.id.txt_RepitePass);
        btn_guardar = dialogCrearPass.findViewById(R.id.btn_CrearPass);
        icono_cerrar = dialogCrearPass.findViewById(R.id.icono_CerrarDialogo);

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password_nueva.getText().toString().equals("") || password_repetida.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Debes completar todos los campos", Toast.LENGTH_LONG).show();
                }else{
                    if(password_nueva.getText().toString().equals(password_repetida.getText().toString())){
                        boolean insertado = usuario.crearPass(password_nueva.getText().toString(), MainActivity.this);
                        if(insertado){
                            dialogCrearPass.dismiss();
                            //Cambiamos el valor en preferencias ya que hemos creado contraseña
                            SharedPreferences prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("password", true);
                            editor.commit();
                            Toast.makeText(getApplicationContext(), "Contraseña creada con éxito", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(getApplicationContext(), "No se ha podido crear la contraseña", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "La contraseña no coincide", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        icono_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCrearPass.dismiss();
            }
        });
    }
}
