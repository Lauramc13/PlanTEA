package com.example.plantea.presentacion.actividades.planificador;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantea.R;
import com.example.plantea.dominio.Categoria;
import com.example.plantea.dominio.Pictograma;
import com.example.plantea.dominio.Planificacion;
import com.example.plantea.presentacion.CrearPlanInterface;
import com.example.plantea.presentacion.actividades.ConfiguracionActivity;
import com.example.plantea.presentacion.actividades.ManualActivity;
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion;
import com.example.plantea.presentacion.fragmentos.CategoriasFragment;
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class CrearPlanActivity extends AppCompatActivity implements CrearPlanInterface, AdaptadorPlanificacion.OnItemSelectedListener {

    int identificadorCategoria;
    boolean subcategoriaOpen = false;

    private TextView labelTitulo;

    FragmentTransaction transaction;
    Fragment fragmentCategorias, fragmentPictogramas, fragmentSubcategoria;
    ArrayList<Pictograma> listaPlanificacion;
    ArrayList<Pictograma> listaPictogramas;

    String tituloPicto;
    String imagenPicto;
    int categoriaPicto;
    //Opcion para indicar funcionalidad editar o crear
    Boolean opcionEditar = false;

    //Variables dialogo crear nuevo pictograma
    Dialog dialogNuevoPictograma;
    TextView titulo_Dialogo;
    Spinner  spinner_Dialogo;
    ImageView img_Picto;
    ImageView img_Cerrar;
    Button btn_Guardar, btn_GuardarPlanificacion;

    //Variables codigo de permiso y acceso a galeria
    private static final int REQUEST_CODIGO_PERMISO = 100;
    private static final int REQUEST_GALERIA = 101;

    TextView txt_TituloPlan;

    //RecyclerView Planificacion
    RecyclerView recyclerView;

    AdaptadorPlanificacion adaptador;

    Pictograma picto = new Pictograma();
    Categoria categoria = new Categoria();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_plan);


        //Activamos icono volver atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_TituloPlan = findViewById(R.id.txt_TituloPlan);
        labelTitulo = findViewById(R.id.lbl_CrearPlanActividad);
        btn_GuardarPlanificacion = findViewById(R.id.btn_guardarPlan);

        listaPictogramas = new ArrayList<>();
        listaPlanificacion = new ArrayList<>();

        //Comprobar si hay parametros en caso de llamada desde editar
        Bundle parametros = this.getIntent().getExtras();
        if(parametros !=null){
            opcionEditar = true;
            txt_TituloPlan.setText( getIntent().getStringExtra("titulo"));
            listaPlanificacion = (ArrayList<Pictograma>) getIntent().getSerializableExtra("pictogramas");
        }

        //Iniciar RecyclerView de planificaciones
        initRecyclerViewPlan();
        int orientation = getResources().getConfiguration().orientation;
        Fragment existingFragmentCategorias;

        if (savedInstanceState == null) {
            // Activity is not being recreated, so create a new instance of PrincipalFragment
            fragmentCategorias = new CategoriasFragment();
            fragmentPictogramas = new CategoriasPictogramasFragment();
            fragmentSubcategoria = new CategoriasPictogramasFragment();
    
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contenedor_fragments, fragmentCategorias);
            transaction.commit();
        } else {
            // Activity is being recreated, so retrieve the existing fragments from the FragmentManager

            if (orientation == Configuration.ORIENTATION_LANDSCAPE){
                existingFragmentCategorias = getSupportFragmentManager().findFragmentById(R.id.categorias);
            } else {
                existingFragmentCategorias = getSupportFragmentManager().findFragmentById(R.id.categoriasPortrait);
            }

            if (existingFragmentCategorias instanceof CategoriasFragment) {
                fragmentCategorias = (CategoriasFragment) existingFragmentCategorias;
            } else {
                fragmentCategorias = new CategoriasFragment();
            }

            fragmentPictogramas = new CategoriasPictogramasFragment();
            fragmentSubcategoria = new CategoriasPictogramasFragment();
        }



        //Dialogo para la creación de un nuevo pictograma
        dialogNuevoPictograma = new Dialog(this);

        //Este método se ejecutará al seleccionar el icono guardar para crear la planificación
        btn_GuardarPlanificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_TituloPlan.getText().toString().isEmpty() || listaPlanificacion.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Necesita añadir un título y pictogramas", Toast.LENGTH_LONG).show();
                }else{
                    //Si la opcionEditar es FALSE se crea una planificación nueva si por el contrario es TRUE se realiza la función editar
                    if (opcionEditar) {
                        Planificacion plan = new Planificacion();
                        plan.actualizarPlanificacion(CrearPlanActivity.this, getIntent().getIntExtra("identificador",0),txt_TituloPlan.getText().toString().toUpperCase(),listaPlanificacion);
                        Toast.makeText(getApplicationContext(), "Planificación " + txt_TituloPlan.getText().toString() + " actualizada" , Toast.LENGTH_LONG).show();
                    } else {
                        Planificacion plan = new Planificacion();
                       boolean creada = plan.crearPlanificacion(CrearPlanActivity.this, listaPlanificacion, txt_TituloPlan.getText().toString().toUpperCase());
                       if(creada){
                           Toast.makeText(getApplicationContext(), "Planificación " + txt_TituloPlan.getText().toString() + " creada" , Toast.LENGTH_LONG).show();
                       }else{
                           Toast.makeText(getApplicationContext(), "Error al crear la planificación", Toast.LENGTH_LONG).show();
                       }
                    }
                    finish();
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.UP) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(listaPlanificacion, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            listaPlanificacion.remove(position);
            recyclerView.getAdapter().notifyItemRemoved(position);
        }
    };

    //Menu principal
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_ayuda:
                Intent i= new Intent(getApplicationContext(), ManualActivity.class);
                startActivity(i);
                break;
            case R.id.item_password:
                Intent intent = new Intent(getApplicationContext(), PasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.item_perfil:
                Intent perfil= new Intent(getApplicationContext(), ConfiguracionActivity.class);
                startActivity(perfil);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    //Creando lista horizontal para la planificacion
    private void initRecyclerViewPlan(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.lst_planificacion);
        recyclerView.setLayoutManager(layoutManager);
        adaptador = new AdaptadorPlanificacion(listaPlanificacion,this);
        recyclerView.setAdapter(adaptador);

        //Desplaza la lista para insertar un nuevo pictograma
        recyclerView.scrollToPosition(adaptador.getItemCount() - 1);

        recyclerView.setOnDragListener(new ChoiceDragListener());

    }

    //Método para mostrar categoria correspondiente
    @Override
    public void mostrarCategoria(int idcategoria) {
        identificadorCategoria = idcategoria;
        listaPictogramas = picto.obtenerPictogramas(this, identificadorCategoria);
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", listaPictogramas);
        fragmentPictogramas.setArguments(bundle);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedor_fragments, fragmentPictogramas);
        transaction.addToBackStack(null); // Se añade a la pila para poder navegar hacia atrás
        transaction.commit();
    }

    //Método para mostrar los pictogramas correspondientes a las categorias de consultas
    @Override
    public void mostrarsubCategoria(String tituloCategoria) {
        subcategoriaOpen = true;
        identificadorCategoria = categoria.obtenerCategoria(this, tituloCategoria);
        listaPictogramas = picto.obtenerPictogramas(this, identificadorCategoria);
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", listaPictogramas);
        fragmentSubcategoria.setArguments(bundle);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedor_fragments, fragmentSubcategoria);
        transaction.addToBackStack(null); // Se añade a la pila para poder navegar hacia atrás
        transaction.commit();

    }

    //Método para cerrar fragment correspondiente
    @Override
    public void cerrarFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        //Comprobamos si es una subcategoria para volver atras o si es categoria normal cerrar
        if(subcategoriaOpen){
            transaction.replace(R.id.contenedor_fragments, fragmentPictogramas);
            subcategoriaOpen = false;
        }else{
            transaction.replace(R.id.contenedor_fragments, fragmentCategorias);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void nuevoPictogramaDialogo() {
        //cerrarFragment(); //Cerrar fragmento al abrir dialogo
        dialogNuevoPictograma.setContentView(R.layout.dialogo_nuevo_pictograma);
        dialogNuevoPictograma.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogNuevoPictograma.show();

        img_Cerrar = dialogNuevoPictograma.findViewById(R.id.icono_CerrarDialogo);
        btn_Guardar = dialogNuevoPictograma.findViewById(R.id.btn_GuardarPicto);
        img_Picto = dialogNuevoPictograma.findViewById(R.id.img_NuevoPicto);
        titulo_Dialogo = dialogNuevoPictograma.findViewById(R.id.txt_Titulo);
        spinner_Dialogo = dialogNuevoPictograma.findViewById(R.id.spinner_Categorias);

        ArrayList categorias = categoria.consultarCategorias(this);
        spinner_Dialogo.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categorias));

        btn_Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(titulo_Dialogo.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Se necesita un título para el nuevo pictograma", Toast.LENGTH_LONG).show();
                } else if (img_Picto.getDrawable() == null){
                    Toast.makeText(getApplicationContext(), "Se necesita una imagen para el nuevo pictograma", Toast.LENGTH_LONG).show();
                } else{
                    String imagen = titulo_Dialogo.getText().toString(); //Nombre de la imagen
                    Bitmap image = ((BitmapDrawable)img_Picto.getDrawable()).getBitmap();

                    //Escalar imagen
                    float proporcion = 500 / (float) image.getWidth();
                    Bitmap imagenFinal = Bitmap.createScaledBitmap(image,500,(int) (image.getHeight() * proporcion),false);

                    //Crear ruta y guardar imagen
                    String ruta = guardarImagen(getApplicationContext(), imagen, imagenFinal);
                    Toast.makeText(getApplicationContext(), "Nuevo pictograma creado", Toast.LENGTH_LONG).show();
                    dialogNuevoPictograma.dismiss(); //Cerrar dialogo

                    //Añadir pictograma
                    picto.nuevoPictograma(CrearPlanActivity.this,  titulo_Dialogo.getText().toString().toUpperCase() ,ruta, spinner_Dialogo.getSelectedItem().toString());
                    //Si la categoria es consultas, estaremos creando una nueva subcategoria
                    if ( spinner_Dialogo.getSelectedItem().toString().equals("CONSULTAS")){
                        categoria.crearCategoria(CrearPlanActivity.this, titulo_Dialogo.getText().toString().toUpperCase());
                    }
                    cerrarFragment();
                    mostrarCategoria(identificadorCategoria);
                }
            }
        });

        img_Cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogNuevoPictograma.dismiss();
            }
        });

        img_Picto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(ActivityCompat.checkSelfPermission(CrearPlanActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        abrirGaleria();
                    }else{
                        ActivityCompat.requestPermissions(CrearPlanActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODIGO_PERMISO);
                    }
                }else {
                    abrirGaleria();
                }
            }
        });
    }

    private void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent, REQUEST_GALERIA);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_GALERIA){
            if(resultCode == Activity.RESULT_OK){
                Uri image = data.getData();
                img_Picto.setImageURI(image);
                img_Picto.setBackground(null);
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

    //Recibe el pictograma a insertar en la planificacion
    @Override
    public void pictogramaSeleccionado(String titulo, String imagen, int categoria) {
        initRecyclerViewPlan();
        tituloPicto = titulo;
        imagenPicto = imagen;
        categoriaPicto = categoria;
    }

    private class ChoiceDragListener implements View.OnDragListener{

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {

            switch (dragEvent.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:

                    Log.i("TAG", "started");
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.i("TAG", "entered");
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                   Log.i("TAG", "exited");
                   break;
                case DragEvent.ACTION_DROP:
                    //imagenPicto="android.resource://com.example.plantea/"+R.drawable.categoria_recompensa;
                    listaPlanificacion.add(new Pictograma(tituloPicto, imagenPicto,categoriaPicto,0));
                    adaptador.notifyDataSetChanged();
                    Log.i("TAG", "drop " );
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.i("TAG", "ended");
                    break;
            }
            return true;
        }
    }
}