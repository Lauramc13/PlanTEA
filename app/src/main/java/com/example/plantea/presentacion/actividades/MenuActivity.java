package com.example.plantea.presentacion.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.plantea.R;
import com.example.plantea.presentacion.actividades.ninio.CuadernoActivity;
import com.example.plantea.presentacion.actividades.ninio.PlanActivity;
import com.example.plantea.presentacion.actividades.planificador.CalendarioActivity;
import com.example.plantea.presentacion.actividades.planificador.PasswordActivity;
import com.example.plantea.presentacion.actividades.ConfiguracionActivity;
import com.example.plantea.presentacion.actividades.MainActivity;
import com.example.plantea.presentacion.actividades.ManualActivity;


public class MenuActivity extends AppCompatActivity {
    private CardView cardCalendario, cardEmociones, cardPlanificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        cardCalendario = findViewById(R.id.card_Calendario);
        cardEmociones = findViewById(R.id.card_Emociones);
        cardPlanificacion = findViewById(R.id.card_Planificacion);

        //Activamos icono volver atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cardCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlanActivity.class);
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

        cardPlanificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalendarioActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_ayuda:
                Intent manual = new Intent(getApplicationContext(), ManualActivity.class);
                startActivity(manual);
                break;
            case R.id.item_password:
                Intent password = new Intent(getApplicationContext(), PasswordActivity.class);
                startActivity(password);
                break;
            case R.id.item_perfil:
                Intent perfil= new Intent(getApplicationContext(), ConfiguracionActivity.class);
                startActivity(perfil);
                break;
            case android.R.id.home:
                Intent it = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(it);
                break;
        }
        return true;
    }



}