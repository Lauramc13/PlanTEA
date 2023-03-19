package com.example.plantea.presentacion.actividades.ninio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plantea.R;
import com.example.plantea.dominio.Pictograma;
import com.example.plantea.presentacion.CuadernoInterface;
import com.example.plantea.presentacion.actividades.ManualActivity;
import com.example.plantea.presentacion.adaptadores.AdaptadorCuadernoActivity;
import com.example.plantea.presentacion.fragmentos.cuaderno.CuadernoPictogramasFragment;
import com.example.plantea.presentacion.fragmentos.cuaderno.PrincipalFragment;

import java.util.ArrayList;

public class CuadernoActivity extends AppCompatActivity implements CuadernoInterface, AdaptadorCuadernoActivity.OnItemSelectedListener {

    FragmentTransaction transaction;
    Fragment fragmentPrincipal, fragmentCuadernoPictogramas;
    ArrayList<Pictograma> listaPictogramas,listaEscala,listaTiempo;
    RecyclerView recyclerView;
    AdaptadorCuadernoActivity adaptador;
    Pictograma picto = new Pictograma();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuaderno);
    
        //Activamos icono volver atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
        listaPictogramas = new ArrayList<>();
        listaEscala = new ArrayList<>();
    
        fragmentCuadernoPictogramas = new CuadernoPictogramasFragment();

        if (savedInstanceState == null) {
            // Activity is not being recreated, so create a new instance of PrincipalFragment
            fragmentPrincipal = new PrincipalFragment();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.layout_fragments, fragmentPrincipal);
            transaction.commit();
        } else {
            // Activity is being recreated, so retrieve the existing fragment from the FragmentManager
            Fragment existingFragment = getSupportFragmentManager().findFragmentById(R.id.layout_fragments);
            if (existingFragment instanceof PrincipalFragment) {
                fragmentPrincipal = (PrincipalFragment) existingFragment;
            } else {
                fragmentPrincipal = new PrincipalFragment();
            }
        }
        //Pictogramas en la parte de arriba del cuaderno
       iniciarListaEscala();
    
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

    public void iniciarListaEscala(){
        picto = new Pictograma();
        listaEscala = picto.obtenerPictogramasCuaderno(this,1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.lst_escala);
        recyclerView.setLayoutManager(layoutManager);
        adaptador = new AdaptadorCuadernoActivity(listaEscala,this);
        recyclerView.setAdapter(adaptador);
    }

    @Override
    public void mostrarPictogramas(int identificador) {
        picto = new Pictograma();
        listaPictogramas = picto.obtenerPictogramasCuaderno(this,identificador);
        iniciarFragment(listaPictogramas);
    }

    //Método para cerrar fragment correspondiente
    @Override
    public void cerrarFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layout_fragments, fragmentPrincipal);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void iniciarFragment(ArrayList pictogramas){
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", pictogramas);

        fragmentCuadernoPictogramas.setArguments(bundle);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layout_fragments, fragmentCuadernoPictogramas);
        transaction.addToBackStack(null); // Se añade a la pila para poder navegar hacia atrás
        transaction.commit();
    }

    @Override
    public void pictogramaCuaderno(int posicion) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogo_presentacion);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView pictograma = dialog.findViewById(R.id.img_pictograma);
        TextView tituloPictograma = dialog.findViewById(R.id.lbl_pictograma);

        pictograma.setImageURI(Uri.parse(listaEscala.get(posicion).getImagen()));
        tituloPictograma.setText(listaEscala.get(posicion).getTitulo());

        dialog.show();
    }
}