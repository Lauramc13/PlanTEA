package com.example.plantea.presentacion.actividades;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;


import com.example.plantea.R;

public class SplashActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_splash_screen);

        //Preferencias
        SharedPreferences prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        Boolean accesoConfiguracion = prefs.getBoolean("configuracion", false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(accesoConfiguracion){
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(SplashActivity.this, ConfiguracionActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        },2000);
    }
}