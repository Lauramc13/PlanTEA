package com.example.plantea.presentacion.actividades.planificador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantea.R;
import com.example.plantea.dominio.Usuario_Planificador;
import com.example.plantea.presentacion.actividades.ManualActivity;

public class PasswordActivity extends AppCompatActivity {

    private TextView viejaPass;
    private TextView nuevaPass;
    private TextView confirmaPass;
    private Button btn_guardar;
    private Boolean actualizado;
    Usuario_Planificador usuario = new Usuario_Planificador();

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
        setContentView(R.layout.activity_password);

        //Activamos icono volver atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viejaPass = findViewById(R.id.txt_PassActual);
        nuevaPass = findViewById(R.id.txt_NuevaPass);
        confirmaPass = findViewById(R.id.txt_RepPass);
        btn_guardar = findViewById(R.id.btn_Guardar);

        //Este método se ejecutará al seleccionar el boton guardar
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viejaPass.getText().toString().equals("") || nuevaPass.getText().toString().equals("") || confirmaPass.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Debes completar todos los campos", Toast.LENGTH_LONG).show();
                }else{
                    actualizado = usuario.confirmarPass(viejaPass.getText().toString(),nuevaPass.getText().toString(),confirmaPass.getText().toString(),PasswordActivity.this);
                    if(actualizado){
                        Toast.makeText(getApplicationContext(), "Contraseña actualizada", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error al actualizar. Introduce de nuevo los datos. ", Toast.LENGTH_LONG).show();
                    }
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
}