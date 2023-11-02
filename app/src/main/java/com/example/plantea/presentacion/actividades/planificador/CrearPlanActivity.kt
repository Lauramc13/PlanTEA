package com.example.plantea.presentacion.actividades.planificador

import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.*
import com.example.plantea.presentacion.CrearPlanInterface
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.ConfiguracionActivity
import com.example.plantea.presentacion.actividades.ManualActivity
import com.example.plantea.presentacion.actividades.PreLoginActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion
import com.example.plantea.presentacion.fragmentos.CategoriasFragment
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment
import kotlinx.coroutines.*
import java.io.*
import java.util.*


class CrearPlanActivity : AppCompatActivity(), CrearPlanInterface, AdaptadorPlanificacion.OnItemSelectedListener{
    var identificadorCategoria = 0
    var subcategoriaOpen = false
    private lateinit var labelTitulo: TextView
    private lateinit var busquedaNula: TextView
    private lateinit var transaction: FragmentTransaction
    private lateinit var fragmentCategorias: Fragment
    private lateinit var fragmentPictogramas: Fragment
    private lateinit var fragmentSubcategoria: Fragment
    private lateinit var fragmentBusqueda: Fragment
    private lateinit var btn_logout: Button
    private lateinit var icono_cerrar_login : AppCompatImageView
    private lateinit var listaPlanificacion: ArrayList<Pictograma>
    private lateinit var listaPictogramas: ArrayList<Pictograma>
    private lateinit var tituloPicto: String
    private lateinit var imagenPicto: String
    private lateinit var searchBar: SearchView
    private lateinit var btn_salir: Button
    private  lateinit var btn_cancelar: Button
    private lateinit var backButton: Button
    private var categoriaPicto = 0
    var isEdited = false
    var pictograma = Pictograma()

    //Opcion para indicar funcionalidad editar o crear
    private var opcionEditar = false

    //Variables dialogo crear nuevo pictograma
    private lateinit var dialogNuevoPictograma: Dialog
    private lateinit var titulo_Dialogo: TextView
    private lateinit var spinner_Dialogo: Spinner
    private lateinit var img_Picto: ImageView
    private lateinit var img_Cerrar: ImageView
    private lateinit var btn_Guardar: Button
    private lateinit var btn_GuardarPlanificacion: Button
    private lateinit var txt_TituloPlan: TextView

    //RecyclerView Planificacion
    lateinit var recyclerView: RecyclerView
    lateinit var adaptador: AdaptadorPlanificacion
    var picto = Pictograma()
    var categoria = Categoria()

   override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Comprobamos la orientacion de la pantalla
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show()
        }
        fragmentCategorias = CategoriasFragment()
        transaction = supportFragmentManager.beginTransaction()
        supportFragmentManager.commit {
            setReorderingAllowed(false)
            replace<CategoriasFragment>(R.id.contenedor_fragments)
        }
        recreate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("arrayListKey", listaPlanificacion)
    }

    private fun getPictogramas(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val dict = CommonUtils.getDataApi(query)

            withContext(Dispatchers.Main) {
                dict.keys.forEach { key ->
                    dict[key]?.let { (value, id) ->
                        crearPictoBusqueda(key, value, id)
                        mostrarBusqueda()
                    }
                }
            }
         }
    }

    private fun mostrarBusqueda() {
        //labelBuscando.visibility = View.GONE
        subcategoriaOpen = false
        val bundle = Bundle()
        bundle.putSerializable("key", listaPictogramas)
        fragmentBusqueda.arguments = bundle
        transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor_fragments, fragmentBusqueda)
        transaction.addToBackStack(null) // Se añade a la pila para poder navegar hacia atrás
        transaction.commit()
    }

    private fun crearPictoBusqueda(bitmap: Bitmap, titulo: String?, id: Int) {
        val tituloMayus = titulo?.uppercase()
        val prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val userId = prefs.getString("idUsuario", "")
        val favorito = pictograma.getFavorito(this, id.toString(), userId)
        val archivo = CommonUtils.crearImagen(bitmap, titulo, this)

        listaPictogramas.add(Pictograma(id.toString(), tituloMayus, archivo, 0, 0, favorito, true))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_plan)

        txt_TituloPlan = findViewById(R.id.txt_TituloPlan)
        if(!opcionEditar) {
            txt_TituloPlan.text = "PLANIFICACIÓN"
        }
        labelTitulo = findViewById(R.id.lbl_CrearPlanActividad)
        btn_GuardarPlanificacion = findViewById(R.id.btn_guardarPlan)
        searchBar = findViewById(R.id.searchViewPicto)
        busquedaNula = findViewById(R.id.busquedaNula)
        backButton = findViewById(R.id.goBackButton)

        listaPictogramas = ArrayList()
        listaPlanificacion = ArrayList()

        backButton.setOnClickListener{
            finish()
        }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getPictogramas(query.trim())
                listaPictogramas.clear()
                val container = supportFragmentManager.findFragmentById(R.id.contenedor_fragments)
                container?.let {
                    supportFragmentManager.beginTransaction()
                        .remove(it)
                        .commit()
                }
                CommonUtils.hideKeyboard(this@CrearPlanActivity, searchBar)
                //labelBuscando.visibility = VISIBLE
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
            opcionEditar = true
            txt_TituloPlan.text = intent.getStringExtra("titulo")
            listaPlanificacion = (intent.getSerializableExtra("pictogramas") as ArrayList<Pictograma>?)!!
        } else if (savedInstanceState != null){
            val savedArrayList = savedInstanceState.getSerializable("arrayListKey") as ArrayList<Pictograma>?
            if (savedArrayList != null) {
                listaPlanificacion = savedArrayList
            }
        }


        //Iniciar RecyclerView de planificaciones
        initRecyclerViewPlan()
        fragmentCategorias = CategoriasFragment()
        fragmentPictogramas = CategoriasPictogramasFragment()
        fragmentSubcategoria = CategoriasPictogramasFragment()
        fragmentBusqueda = CategoriasPictogramasFragment()

        transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.contenedor_fragments, fragmentCategorias)
        transaction.commit()

        //Dialogo para la creación de un nuevo pictograma
        dialogNuevoPictograma = Dialog(this)

        //Este método se ejecutará al seleccionar el icono guardar para crear la planificación
        btn_GuardarPlanificacion.setOnClickListener {
            if (txt_TituloPlan.text.toString().isEmpty() || listaPlanificacion.isEmpty()) {
                Toast.makeText(applicationContext, "Necesita añadir un título y pictogramas", Toast.LENGTH_LONG).show()
            } else {
                //Si la opcionEditar es FALSE se crea una planificación nueva si por el contrario es TRUE se realiza la función editar
                if (opcionEditar) {
                    val plan = Planificacion()
                    plan.actualizarPlanificacion(
                        this@CrearPlanActivity,
                        intent.getIntExtra("identificador", 0),
                        txt_TituloPlan.text.toString().uppercase(Locale.getDefault()),
                        listaPlanificacion
                    )

                    Toast.makeText(applicationContext, "Planificación " + txt_TituloPlan.text.toString() + " actualizada", Toast.LENGTH_LONG).show()
                } else {
                    val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                    val idUsuario = prefs.getString("idUsuario", "")

                    val plan = Planificacion()
                    val idPlan = idUsuario?.let { it1 ->
                        plan.crearPlanificacion(this@CrearPlanActivity,  it1,txt_TituloPlan.text.toString().uppercase(Locale.getDefault()))
                    }
                    val creada = plan.addPictogramasPlan(idPlan, this@CrearPlanActivity, listaPlanificacion)

                    if (creada == true) {
                        Toast.makeText(
                            applicationContext,
                            "Planificación " + txt_TituloPlan.text.toString() + " creada",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Error al crear la planificación",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                finish()
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    var simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.START or ItemTouchHelper.END, ItemTouchHelper.UP) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            val fromPosition = viewHolder.absoluteAdapterPosition
            val toPosition = target.absoluteAdapterPosition
            Collections.swap(listaPlanificacion, fromPosition, toPosition)
            isEdited = true
            recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            listaPlanificacion.removeAt(position)
            isEdited = true
            recyclerView.adapter!!.notifyItemRemoved(position)
        }
    }
/*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_ayuda -> {
                val i = Intent(applicationContext, ManualActivity::class.java)
                startActivity(i)
            }
            R.id.item_perfil -> {
                val popupMenu = PopupMenu(this@CrearPlanActivity, findViewById(R.id.item_ayuda) )
                popupMenu.inflate(R.menu.popup_menu)

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.option_1 -> {
                            val perfil = Intent(applicationContext, ConfiguracionActivity::class.java)
                            startActivity(perfil)
                            true
                        }
                        R.id.option_3 -> {
                            val dialogLogout = Dialog(this)
                            dialogLogout.setContentView(R.layout.dialogo_logout)
                            dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            btn_logout = dialogLogout.findViewById(R.id.btn_logout)
                            icono_cerrar_login = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
                            btn_logout.setOnClickListener {
                                val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                                prefs.edit().clear().apply()
                                // val editor = prefs.edit()
                                // editor.putBoolean("userAccount", false)
                                // editor.apply()
                                val login = Intent(applicationContext, PreLoginActivity::class.java)
                                startActivity(login)
                            }
                            icono_cerrar_login.setOnClickListener { dialogLogout.dismiss() }
                            dialogLogout.show()
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
            android.R.id.home -> {
                if (!isEdited) {
                    finish()
                } else {
                    val dialogSalir = Dialog(this)
                    dialogSalir.setContentView(R.layout.dialogo_salir_planificacion)
                    dialogSalir.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    btn_salir = dialogSalir.findViewById(R.id.btn_salir)
                    icono_cerrar_login = dialogSalir.findViewById(R.id.icono_CerrarDialogoSalir)
                    btn_cancelar = dialogSalir.findViewById(R.id.btn_cancelarSalir)
                    btn_salir.setOnClickListener {
                        dialogSalir.dismiss()
                        finish()
                    }
                    btn_cancelar.setOnClickListener { dialogSalir.dismiss() }
                    icono_cerrar_login.setOnClickListener { dialogSalir.dismiss() }

                    dialogSalir.show()
                }
            }

        }
        return true
    }
*/
    //Creando lista horizontal para la planificacion
    private fun initRecyclerViewPlan() {

        val layoutManager: LinearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView = findViewById(R.id.lst_planificacion)
        recyclerView.layoutManager = layoutManager
        adaptador = AdaptadorPlanificacion(listaPlanificacion)
        recyclerView.adapter = adaptador

        //Desplaza la lista para insertar un nuevo pictograma
        recyclerView.scrollToPosition(adaptador.itemCount - 1)
        //recyclerView.setOnDragListener(ChoiceDragListener())
    }

    //Método para mostrar categoria correspondiente
    override fun mostrarCategoria(idCategoria: Int) {
        //labelBuscando.visibility = View.GONE
        identificadorCategoria = idCategoria
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        val idUsuario = prefs.getString("idUsuario", "")
        if (idCategoria == 10) {
            listaPictogramas = picto.obtenerFavoritos(this, idUsuario)
        } else {
            listaPictogramas = picto.obtenerPictogramas(this, identificadorCategoria, idUsuario) as ArrayList<Pictograma>
        }
        val bundle = Bundle()
        bundle.putSerializable("key", listaPictogramas)
        fragmentPictogramas.arguments = bundle
        transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor_fragments, fragmentPictogramas)
        transaction.addToBackStack(null) // Se añade a la pila para poder navegar hacia atrás
        transaction.commit()
    }

    //Método para mostrar los pictogramas correspondientes a las categorias de consultas
    override fun mostrarsubCategoria(tituloCategoria: String?) {
        //labelBuscando.visibility = View.GONE
        subcategoriaOpen = true
        identificadorCategoria = categoria.obtenerCategoria(this, tituloCategoria)
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        val idUsuario = prefs.getString("idUsuario", "")
        listaPictogramas = picto.obtenerPictogramas(this, identificadorCategoria, idUsuario) as ArrayList<Pictograma>
        //get favourite for each pictogram
        // for (pictograma in listaPictogramas) {
        //     Log.d("asf", "listaPictogramas: ${pictograma.favorito}")
        // }

        val bundle = Bundle()
        bundle.putSerializable("key", listaPictogramas)
        fragmentSubcategoria.arguments = bundle
        transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor_fragments, fragmentSubcategoria)
        transaction.addToBackStack(null) // Se añade a la pila para poder navegar hacia atrás
        transaction.commit()
    }

    //Método para cerrar fragment correspondiente
    override fun cerrarFragment() {
        transaction = supportFragmentManager.beginTransaction()
        //Comprobamos si es una subcategoria para volver atras o si es categoria normal cerrar
        if (subcategoriaOpen) {
            transaction.replace(R.id.contenedor_fragments, fragmentPictogramas)
            subcategoriaOpen = false
        } else {
            transaction.replace(R.id.contenedor_fragments, fragmentCategorias)
        }
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun nuevoPictogramaDialogo() {
        //cerrarFragment(); //Cerrar fragmento al abrir dialogo
        dialogNuevoPictograma.setContentView(R.layout.dialogo_nuevo_pictograma)
        dialogNuevoPictograma.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogNuevoPictograma.show()
        img_Cerrar = dialogNuevoPictograma.findViewById(R.id.icono_CerrarDialogo)
        btn_Guardar = dialogNuevoPictograma.findViewById(R.id.btn_GuardarPicto)
        img_Picto = dialogNuevoPictograma.findViewById(R.id.img_NuevoPicto)
        titulo_Dialogo = dialogNuevoPictograma.findViewById(R.id.txt_Titulo)
        spinner_Dialogo = dialogNuevoPictograma.findViewById(R.id.spinner_Categorias)
        val categorias = categoria.consultarCategorias(this)
        spinner_Dialogo
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categorias  as ArrayList<String>
        )
        spinner_Dialogo.adapter = adapter
        btn_Guardar.setOnClickListener {
            if (titulo_Dialogo.text.toString().isEmpty()) {
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
                val imagen = titulo_Dialogo.text.toString() //Nombre de la imagen
                val image = (img_Picto.drawable as BitmapDrawable).bitmap

                //Escalar imagen
                val proporcion = 500 / image.width.toFloat()
                val imagenFinal = Bitmap.createScaledBitmap(
                    image,
                    500,
                    (image.height * proporcion).toInt(),
                    false
                )

                //Crear ruta y guardar imagen
                val ruta = guardarImagen(applicationContext, imagen, imagenFinal)
                Toast.makeText(applicationContext, "Nuevo pictograma creado", Toast.LENGTH_LONG)
                    .show()
                dialogNuevoPictograma.dismiss() //Cerrar dialogo

                val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                val idUsuario = prefs.getString("idUsuario", "")
                //Añadir pictograma
                picto.nuevoPictograma(
                    this@CrearPlanActivity,
                    titulo_Dialogo.text.toString().uppercase(Locale.getDefault()),
                    ruta,
                    spinner_Dialogo.selectedItem.toString(),
                    idUsuario
                )
                //Si la categoria es consultas, estaremos creando una nueva subcategoria
                if (spinner_Dialogo.selectedItem.toString() == "CONSULTAS") {
                    categoria.crearCategoria(
                        this@CrearPlanActivity,
                        titulo_Dialogo.text.toString().uppercase(Locale.getDefault())
                    )
                }
                cerrarFragment()
                mostrarCategoria(identificadorCategoria)
            }
        }
        img_Cerrar.setOnClickListener { dialogNuevoPictograma.dismiss() }
        img_Picto.setOnClickListener {
            abrirGaleria()

        }
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

    private fun guardarImagen(context: Context, nombre: String, imagen: Bitmap): String {
        val cw = ContextWrapper(context)
        val dirImages = cw.getDir("Imagenes", MODE_PRIVATE)
        val myPath = File(dirImages, "$nombre.png")
        val fos: FileOutputStream?
        try {
            fos = FileOutputStream(myPath)
            imagen.compress(Bitmap.CompressFormat.PNG, 10, fos) // calidad a 0 imagen mas pequeña
            fos.flush()
        } catch (ex: FileNotFoundException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return myPath.absolutePath
    }


    //Recibe el pictograma a insertar en la planificacion
    override fun pictogramaSeleccionado(titulo: String?, imagen: String?, categoria: Int) {
        initRecyclerViewPlan()
        if (titulo != null) {
            tituloPicto = titulo
        }
        if (imagen != null) {
            imagenPicto = imagen
        }
        categoriaPicto = categoria
    }

    // private inner class ChoiceDragListener : OnDragListener {
    //     override fun onDrag(view: View, dragEvent: DragEvent): Boolean {
    //         when (dragEvent.action) {
    //             DragEvent.ACTION_DRAG_STARTED -> Log.i("TAG", "started")
    //             DragEvent.ACTION_DRAG_ENTERED -> Log.i("TAG", "entered")
    //             DragEvent.ACTION_DRAG_EXITED -> Log.i("TAG", "exited")
    //             DragEvent.ACTION_DROP -> {
    //                 //imagenPicto="android.resource://com.example.plantea/"+R.drawable.categoria_recompensa;
    //                 listaPlanificacion.add(Pictograma(tituloPicto, imagenPicto, categoriaPicto, 0))
    //                 adaptador.notifyDataSetChanged()
    //                 Log.i("TAG", "drop ")
    //             }
    //             DragEvent.ACTION_DRAG_ENDED -> Log.i("TAG", "ended")
    //         }
    //         return true
    //     }
    // }

    fun addPictogram(pictogram: Pictograma) {
        listaPlanificacion.add(pictogram)
        isEdited = true
        adaptador!!.notifyDataSetChanged()
    }


}
