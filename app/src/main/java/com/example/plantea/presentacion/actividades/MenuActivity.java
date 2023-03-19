package com.example.plantea.presentacion.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.plantea.R;
import com.example.plantea.presentacion.actividades.ninio.CuadernoActivity;
import com.example.plantea.presentacion.actividades.planificador.CalendarioActivity;
import com.example.plantea.presentacion.actividades.planificador.PasswordActivity;


public class MenuActivity extends AppCompatActivity {
    private CardView cardCalendario, cardEmociones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        cardCalendario = findViewById(R.id.card_Calendario);
        cardEmociones = findViewById(R.id.card_Emociones);

        //Activamos icono volver atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cardCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalendarioActivity.class);
                startActivity(intent);
            }
        });

        cardEmociones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CuadernoActivity.class);
                startActivity(intent);
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent it = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(it);
                break;
        }
        return true;
    }



}