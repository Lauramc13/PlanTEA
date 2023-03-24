package com.example.plantea.presentacion.actividades.ninio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantea.R;
import com.example.plantea.dominio.Pictograma;
import com.example.plantea.dominio.Planificacion;
import com.example.plantea.presentacion.actividades.MainActivity;
import com.example.plantea.presentacion.actividades.ManualActivity;
import com.example.plantea.presentacion.actividades.planificador.PasswordActivity;
import com.example.plantea.presentacion.adaptadores.AdaptadorPresentacion;

import java.util.ArrayList;
import java.util.Stack;

public class PlanActivity extends AppCompatActivity implements AdaptadorPresentacion.OnItemSelectedListener{

    RecyclerView recyclerPresentacionPlan;
    ArrayList<Pictograma> listaPictogramas;
    Planificacion plan = new Planificacion();
    TextView titulo,mensajePremio,txt_objetoAyuda, lblMensaje;
    String tituloObtenido;
    ImageView iconoCuaderno, iconoDeshacer,imagenConfeti, img_objetoAyuda;
    CardView card;
    LinearLayout objetoAyuda;
    Stack pasosCompletados;
    AdaptadorPresentacion adaptador;

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
        setContentView(R.layout.activity_plan);

        //Activamos icono volver atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Pila de los pasos completados en el seguimiento de un plan
        pasosCompletados = new Stack();

        iconoCuaderno = findViewById(R.id.img_cuaderno);
        iconoDeshacer = findViewById(R.id.icon_deshacer);
        img_objetoAyuda = findViewById(R.id.img_objetoAyuda);
        titulo = findViewById(R.id.lbl_titulo);
        txt_objetoAyuda = findViewById(R.id.txt_objetoAyuda);
        lblMensaje = findViewById(R.id.lbl_mensajeNinio);
        recyclerPresentacionPlan =  findViewById(R.id.recycler_plan);
        int orientation = getResources().getConfiguration().orientation;
        int gridValueManager;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridValueManager = 3; // set the number of columns to 2 for portrait mode
        } else {
            gridValueManager = 5; // set the number of columns to 3 for landscape mode
        }

        recyclerPresentacionPlan.setLayoutManager(new GridLayoutManager(this, 3));

        objetoAyuda = findViewById(R.id.layout_objetoAyuda);

        //Obtener preferencias objeto tranquilizador
        SharedPreferences prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        img_objetoAyuda.setImageURI(Uri.parse(prefs.getString("imagenObjeto","")));
        txt_objetoAyuda.setText(prefs.getString("nombreObjeto","").toUpperCase());

        //Comprobar si hay parametros en caso de llamada desde el planificador
        Bundle parametros = this.getIntent().getExtras();
        if(parametros !=null){
            titulo.setText(getIntent().getStringExtra("titulo"));
            listaPictogramas = (ArrayList<Pictograma>) getIntent().getSerializableExtra("pictogramas");
        }else{
            //Mostrar la planificación a seguir para el niño
            listaPictogramas = new ArrayList<>();
            listaPictogramas = plan.mostrarPlanificacion(this);
            //Mostrar título de la planificación
            tituloObtenido = plan.obtenerTituloPlan(this);
            titulo.setText(tituloObtenido);
        }
        adaptador = new AdaptadorPresentacion(listaPictogramas,this);
        recyclerPresentacionPlan.setAdapter(adaptador);
        //Mostrar mensaje si no hay plan
        if (listaPictogramas.isEmpty()){
            lblMensaje.setVisibility(View.VISIBLE);
        }else{
            lblMensaje.setVisibility(View.INVISIBLE);
        }

        //Este método se ejecutará al seleccionar el icono cuaderno para acceder
        iconoCuaderno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CuadernoActivity.class);
                startActivity(intent);
            }
        });

        //Este método se ejecutará al seleccionar el icono deshacer para volver un paso atrás en el seguimiento
        iconoDeshacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pasosCompletados.empty()){
                   int posicionUndo= (int) pasosCompletados.pop();
                    AdaptadorPresentacion.ViewHolderPictogramas viewHolderPictogramas = (AdaptadorPresentacion.ViewHolderPictogramas) recyclerPresentacionPlan.findViewHolderForAdapterPosition(posicionUndo);
                    viewHolderPictogramas.itemView.findViewById(R.id.id_Imagen).setAlpha(1f);
                    viewHolderPictogramas.itemView.findViewById(R.id.id_Texto).setAlpha(1f);
                    viewHolderPictogramas.itemView.findViewById(R.id.id_card).setBackgroundResource(R.drawable.card_personalizado);
                }
            }
        });

        objetoAyuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(PlanActivity.this);
                dialog.setContentView(R.layout.dialogo_presentacion);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView pictograma = dialog.findViewById(R.id.img_pictograma);
                TextView tituloPictograma = dialog.findViewById(R.id.lbl_pictograma);
                pictograma.setImageURI(Uri.parse(prefs.getString("imagenObjeto","")));
                tituloPictograma.setText(prefs.getString("nombreObjeto","").toUpperCase());
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ayuda, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_ayuda_menu:
                Intent i = new Intent(getApplicationContext(), ManualActivity.class);
                startActivity(i);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


    @Override
    public void onItemSeleccionado(int posicion) {

        //Añade a la pila el paso completado
        pasosCompletados.push(posicion);

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogo_presentacion);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imagenConfeti = dialog.findViewById(R.id.img_confeti);
        mensajePremio = dialog.findViewById(R.id.txt_premio);
        card = dialog.findViewById(R.id.card_presentacion);

        Animation animFondo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.confeti);
        Animation animCard = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card);


        //Si es recompensa mostramos el dialogo diferente
        if(listaPictogramas.get(posicion).getCategoria() == 7){
            imagenConfeti.setVisibility(View.VISIBLE);
            mensajePremio.setVisibility(View.VISIBLE);
            imagenConfeti.setAnimation(animFondo);
            card.setAnimation(animCard);
            mensajePremio.setAnimation(animFondo);

        } else if(listaPictogramas.get(posicion).getCategoria() == 6){
            imagenConfeti.setVisibility(View.VISIBLE);
            mensajePremio.setVisibility(View.VISIBLE);
            imagenConfeti.setImageResource(R.drawable.espera);
            mensajePremio.setText("¡Mientras esperamos!");
            imagenConfeti.setAnimation(animCard);
            card.setAnimation(animCard);
            mensajePremio.setAnimation(animFondo);
        } else{
            imagenConfeti.setVisibility(View.INVISIBLE);
            mensajePremio.setVisibility(View.INVISIBLE);
        }

        ImageView pictograma = dialog.findViewById(R.id.img_pictograma);
        TextView tituloPictograma = dialog.findViewById(R.id.lbl_pictograma);
        pictograma.setImageURI(Uri.parse(listaPictogramas.get(posicion).getImagen()));
        tituloPictograma.setText(listaPictogramas.get(posicion).getTitulo());
        dialog.show();
    }
}

