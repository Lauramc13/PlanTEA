package com.example.plantea.presentacion.actividades

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion
import com.example.plantea.presentacion.fragmentos.CategoriasFragment
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment
import com.example.plantea.presentacion.viewModels.CrearPlanViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections
import java.util.Locale


class CrearPlanActivity : AppCompatActivity(), AdaptadorPlanificacion.OnItemSelectedListener{
    private lateinit var labelTitulo: TextView
    private lateinit var busquedaNula: TextView
    private lateinit var transaction: FragmentTransaction

    private lateinit var searchBar: SearchView
    private lateinit var backButton: Button

    //Variables dialogo crear nuevo pictograma
    private lateinit var titulo_Dialogo: TextInputLayout
    private lateinit var spinner_Dialogo: Spinner
    private lateinit var img_Picto: ImageView
    private lateinit var img_Cerrar: ImageView
    private lateinit var btn_Guardar: Button
    private lateinit var btn_GuardarPlanificacion: Button
    private lateinit var txt_TituloPlan: TextView

    //RecyclerView Planificacion
    lateinit var recyclerView: RecyclerView
    lateinit var adaptador: AdaptadorPlanificacion

    private val viewModel by viewModels<CrearPlanViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_plan)

        labelTitulo = findViewById(R.id.lbl_CrearPlanActividad)
        btn_GuardarPlanificacion = findViewById(R.id.btn_guardarPlan)
        searchBar = findViewById(R.id.searchViewPicto)
        busquedaNula = findViewById(R.id.busquedaNula)
        backButton = findViewById(R.id.goBackButton)
        txt_TituloPlan = findViewById(R.id.txt_TituloPlan)

        backButton.setOnClickListener{
            finish()
        }

        observers()

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.setIdUsuario(prefs)

        val callback = viewModel.callBackActivity(this)
        onBackPressedDispatcher.addCallback(this, callback)

        // SearchView para buscar pictogramas
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getPictogramas(query.trim())
                CommonUtils.hideKeyboard(this@CrearPlanActivity, searchBar)
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                newText.trim()
                return true
            }
        })


        //Comprobar si hay parametros en caso de llamada desde editar
        val parametros = this.intent.extras
        if (parametros != null) {
            viewModel.opcionEditar = true
            txt_TituloPlan.text = intent.getStringExtra("titulo")
            viewModel.listaPlanificacion = (intent.getSerializableExtra("pictogramas") as ArrayList<Pictograma>?)!!
        }else{
            txt_TituloPlan.text = "PLANIFICACIÓN"
        }

        //Iniciar RecyclerView de planificaciones
        initRecyclerViewPlan()

        if(savedInstanceState == null){
            viewModel.fragment = CategoriasFragment()
            transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.contenedor_fragments, CategoriasFragment()).commit()
        }else{
            viewModel.fragment = supportFragmentManager.findFragmentById(R.id.contenedor_fragments)!!
        }

        //Este método se ejecutará al seleccionar el icono guardar para crear la planificación
        btn_GuardarPlanificacion.setOnClickListener {
            clickGuardarPicto()
        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    // Callback para el drag and drop de la planificacion y el swipe para eliminar
    private var simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.START or ItemTouchHelper.END, ItemTouchHelper.UP) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            val fromPosition = viewHolder.absoluteAdapterPosition
            val toPosition = target.absoluteAdapterPosition
            Collections.swap(viewModel.listaPlanificacion, fromPosition, toPosition)
            viewModel.isEdited = true
            recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            viewModel.listaPlanificacion.removeAt(position)
            viewModel.isEdited = true
            recyclerView.adapter!!.notifyItemRemoved(position)
        }
    }

    //Creando lista horizontal para la planificacion
    private fun initRecyclerViewPlan() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView = findViewById(R.id.lst_planificacion)
        recyclerView.layoutManager = layoutManager
        adaptador = AdaptadorPlanificacion(viewModel.listaPlanificacion)
        recyclerView.adapter = adaptador

        //Desplaza la lista para insertar un nuevo pictograma
        recyclerView.scrollToPosition(adaptador.itemCount - 1)
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        // Handle the returned URI here
        if (uri != null) {
            val image = uri
            img_Picto.setImageURI(image)
            img_Picto.background = null
        } else {
            Toast.makeText(this, "No se ha seleccionado una imagen", Toast.LENGTH_SHORT).show()
        }
    }

    fun observers() {
        viewModel._identificadorCategoria.observe(this) {
            transaction = supportFragmentManager.beginTransaction()
            viewModel.cambiarFragment(transaction, false)
        }

        viewModel._identificadorSubCategoria.observe(this) {//TODO: ver si se quita esto
            transaction = supportFragmentManager.beginTransaction()
            viewModel.subcategoriaOpen = true
            viewModel.cambiarFragment(transaction, true)
        }

        viewModel._closeFragment.observe(this) {
            if(it){
                transaction = supportFragmentManager.beginTransaction()
                viewModel.closeFragment(transaction, this)
            }
        }

        viewModel._clearBusqueda.observe(this) {
            if(it){
                searchBar.setQuery("", false)
                searchBar.clearFocus()
            }
        }

        viewModel._pictogramaSeleccionado.observe(this) {
            adaptador.notifyDataSetChanged()
            initRecyclerViewPlan()
            viewModel.tituloPicto = it.titulo!!
            viewModel.imagenPicto = it.imagen!!
            viewModel.categoriaPicto = it.categoria
        }

        viewModel._nuevoPictoDialog.observe(this) {
            if (it) {
                //cerrarFragment(); //Cerrar fragmento al abrir dialogo
                val dialogNuevoPictograma = Dialog(this)
                dialogNuevoPictograma.setContentView(R.layout.dialogo_nuevo_pictograma)
                dialogNuevoPictograma.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialogNuevoPictograma.show()
                img_Cerrar = dialogNuevoPictograma.findViewById(R.id.icono_CerrarDialogo)
                btn_Guardar = dialogNuevoPictograma.findViewById(R.id.btn_GuardarPicto)
                img_Picto = dialogNuevoPictograma.findViewById(R.id.img_NuevoPicto)
                titulo_Dialogo = dialogNuevoPictograma.findViewById(R.id.txt_Titulo)
                spinner_Dialogo = dialogNuevoPictograma.findViewById(R.id.spinner_Categorias)
                val categorias = viewModel.categoria.consultarCategorias(this)
               // spinner_Dialogo

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias as ArrayList<String>)
                spinner_Dialogo.adapter = adapter

                btn_Guardar.setOnClickListener {
                    if (titulo_Dialogo.editText?.text.toString().isEmpty()) {
                        Toast.makeText(
                            applicationContext,
                            "Se necesita un título para el nuevo pictograma",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (img_Picto.drawable == null) {
                        Toast.makeText(
                            applicationContext,
                            "Se necesita una imagen para el nuevo pictograma",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val imagen = titulo_Dialogo.editText?.text.toString() //Nombre de la imagen
                        val image = (img_Picto.drawable as BitmapDrawable).bitmap

                        val ruta = viewModel.guardarImagen(applicationContext, imagen, image)

                        Toast.makeText(applicationContext, "Nuevo pictograma creado", Toast.LENGTH_LONG).show()

                        viewModel._nuevoPictoDialog.value = false
                        dialogNuevoPictograma.dismiss() //Cerrar dialogo

                        //Añadir pictograma
                        viewModel.pictograma.nuevoPictograma(
                            this@CrearPlanActivity,
                            titulo_Dialogo.editText?.text.toString().uppercase(Locale.getDefault()),
                            ruta,
                            spinner_Dialogo.selectedItem.toString(),
                            viewModel.idUsuario
                        )

                        //Si la categoria es consultas, estaremos creando una nueva subcategoria
                        if (spinner_Dialogo.selectedItem.toString() == "CONSULTAS") {
                            viewModel.categoria.crearCategoria(
                                this@CrearPlanActivity,
                                titulo_Dialogo.editText?.text.toString()
                                    .uppercase(Locale.getDefault())
                            )
                        }
                        viewModel._identificadorCategoria.value?.let { it1 -> viewModel.mostrarCategoria(it1, this)
                        }
                    }
                }

                img_Cerrar.setOnClickListener {
                    dialogNuevoPictograma.dismiss()
                }

                img_Picto.setOnClickListener {
                    abrirGaleria()
                }
            }
        }
    }

    private fun clickGuardarPicto(){
        if (txt_TituloPlan.text.toString().isEmpty() || viewModel.listaPlanificacion.isEmpty()) {
            Toast.makeText(applicationContext, "Necesita añadir un título y pictogramas", Toast.LENGTH_LONG).show()
        } else {
            //Si la opcionEditar es FALSE se crea una planificación nueva si por el contrario es TRUE se realiza la función editar
            if (viewModel.opcionEditar) {
                viewModel.planificacion.actualizarPlanificacion(this@CrearPlanActivity, intent.getIntExtra("identificador", 0), txt_TituloPlan.text.toString().uppercase(Locale.getDefault()), viewModel.listaPlanificacion)
                Toast.makeText(applicationContext, "Planificación " + txt_TituloPlan.text.toString() + " actualizada", Toast.LENGTH_LONG).show()
            } else {
                val idPlan = viewModel.planificacion.crearPlanificacion(this@CrearPlanActivity,  viewModel.idUsuario,txt_TituloPlan.text.toString().uppercase(Locale.getDefault()))
                val creada = viewModel.planificacion.addPictogramasPlan(idPlan, this@CrearPlanActivity, viewModel.listaPlanificacion)

                if (creada == true) {
                    Toast.makeText(applicationContext, "Planificación " + txt_TituloPlan.text.toString() + " creada", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Error al crear la planificación", Toast.LENGTH_LONG).show()
                }
            }

            finish()
        }

    }

    fun getPictogramas(query: String) {
        viewModel.listaPictogramas.clear()
        CoroutineScope(Dispatchers.IO).launch {
            val dict = CommonUtils.getDataApi(query)

            withContext(Dispatchers.Main) {
                dict.keys.mapNotNull { key ->
                    dict[key]?.let { (value, id) ->
                        viewModel.crearPictoBusqueda(key, value, id, this@CrearPlanActivity)
                    }
                }

                viewModel.busquedaOpen = true
                //find fragment by id
                val fragment = supportFragmentManager.findFragmentById(R.id.contenedor_fragments)
                transaction = supportFragmentManager.beginTransaction()

                if (fragment != null) {
                    viewModel.cambiarFragmentBusqueda(transaction, fragment)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel._closeFragment.value = false
    }



}
