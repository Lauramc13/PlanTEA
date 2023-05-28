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
import android.view.DragEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnDragListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.*
import com.example.plantea.presentacion.ApiInterface
import com.example.plantea.presentacion.CrearPlanInterface
import com.example.plantea.presentacion.actividades.ConfiguracionActivity
import com.example.plantea.presentacion.actividades.ManualActivity
import com.example.plantea.presentacion.actividades.PreLoginActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion
import com.example.plantea.presentacion.fragmentos.CategoriasFragment
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment
import kotlinx.coroutines.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.util.*


class CrearPlanActivity : AppCompatActivity(), CrearPlanInterface, AdaptadorPlanificacion.OnItemSelectedListener {
    var identificadorCategoria = 0
    var subcategoriaOpen = false
    private lateinit var labelTitulo: TextView
    //lateinit var labelBuscando: TextView
    lateinit var transaction: FragmentTransaction
    lateinit var fragmentCategorias: Fragment
    lateinit var fragmentPictogramas: Fragment
    lateinit var fragmentSubcategoria: Fragment
    lateinit var fragmentBusqueda: Fragment
    lateinit var btn_logout: Button
    lateinit var icono_cerrar_login : AppCompatImageView
    lateinit var listaPlanificacion: ArrayList<Pictograma>
    lateinit var listaBusqueda: ArrayList<Pictograma>
    lateinit var listaPictogramas: ArrayList<Pictograma>
    lateinit var tituloPicto: String
    lateinit var imagenPicto: String
    lateinit var searchBar: SearchView
    var categoriaPicto = 0

    //Opcion para indicar funcionalidad editar o crear
    var opcionEditar = false

    //Variables dialogo crear nuevo pictograma
    lateinit var dialogNuevoPictograma: Dialog
    lateinit var titulo_Dialogo: TextView
    lateinit var spinner_Dialogo: Spinner
    lateinit var img_Picto: ImageView
    lateinit var img_Cerrar: ImageView
    lateinit var btn_Guardar: Button
    lateinit var btn_GuardarPlanificacion: Button
    lateinit var txt_TituloPlan: TextView

    //RecyclerView Planificacion
    lateinit var recyclerView: RecyclerView
    lateinit var adaptador: AdaptadorPlanificacion
    var picto = Pictograma()
    var categoria = Categoria()

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
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

    private fun getPictogramas(query: String) {
        Log.d("TAG", "1")

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.arasaac.org/api/")
            .build()
            .create(ApiInterface::class.java)

        val retrofitBuilderImg = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://static.arasaac.org/pictograms/")
            .build()
            .create(ApiInterface::class.java)

        val dict = mutableMapOf<Bitmap, String>()
        var listaIds = mutableListOf<Int>()
        var listaTitulos = mutableListOf<String>()

        CoroutineScope(Dispatchers.IO).launch {
            val job: Job = CoroutineScope(Dispatchers.IO).async {
                val retrofitData = retrofitBuilder.getData(query)
                Log.d("TAG", "2")
                try {
                    val response = retrofitData.execute()
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            for (jsonData in responseBody) {
                                val id = jsonData._id
                                listaIds.add(id)
                                val keyword = jsonData.keywords[0].keyword
                                listaTitulos.add(keyword)
                            }
                        }
                    }
                } catch (e: IOException) {
                    // Handle exception
                }

                val imageJobs = listaIds.map { id ->
                    val keyword = listaTitulos[listaIds.indexOf(id)]
                    CoroutineScope(Dispatchers.IO).async {
                        val retrofitImage = retrofitBuilderImg.getImage(id.toString())
                        try {
                            val response = retrofitImage.execute()
                            if (response.isSuccessful) {
                                val bitmap = BitmapFactory.decodeStream(
                                    response.body()!!.byteStream()
                                )
                                dict[bitmap] = keyword
                            } else {
                                Log.d("TAG", "Error de la llamada a las imagenes")
                            }
                        } catch (e: IOException) {
                            // Handle exception
                        }
                    }
                }

                imageJobs.awaitAll()
                Log.d("TAG", "3")
            }

            job.join()
            Log.d("TAG", "4")




            dict.keys.forEachIndexed { index, key ->
                val asf = dict.keys.size
                Log.d("TAG", "$index size: $asf")


                val value = dict[key]
                if (value != null) {
                    crearPictoBusqueda(key, value)
                    //mostrarBusqueda() //TODO: SE QUEDA AQUI DE PRUEBA POR AHORA
                }
                if (index == dict.keys.size - 1) {
                    mostrarBusqueda()
                }
            }
        //     if(dict.keys.size == 0){
        //         labelBuscando.text = "No se han encontrado pictogramas"
        //     }else{
        //         labelBuscando.visibility = View.GONE
        //     }
         }


    }

    fun mostrarBusqueda() {
        Log.d("TAG", "SE EJECUTA MOSTRAR BUSQUEDA")
        //labelBuscando.visibility = View.GONE
        subcategoriaOpen = false
        val bundle = Bundle()
        bundle.putSerializable("key", listaBusqueda)
        fragmentBusqueda.arguments = bundle
        transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor_fragments, fragmentBusqueda)
        transaction.addToBackStack(null) // Se añade a la pila para poder navegar hacia atrás
        transaction.commit()
    }

    fun crearPictoBusqueda(bitmap: Bitmap, titulo: String?) {
        val width = bitmap.width
        val height = bitmap.height
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val numero = UUID.randomUUID()

        val filename = "$titulo$numero.jpg"
        val outputStream: FileOutputStream
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE)
            outputBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val tituloMayus = titulo?.uppercase()
        val archivo = getFileStreamPath(filename).absolutePath
        listaBusqueda.add(Pictograma(tituloMayus, archivo, 0, 0))
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(searchBar.windowToken, 0)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_plan)

        //Activamos icono volver atrás
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        txt_TituloPlan = findViewById(R.id.txt_TituloPlan)
        txt_TituloPlan.text = "PLANIFICACIÓN" //TODO
        labelTitulo = findViewById(R.id.lbl_CrearPlanActividad)
        btn_GuardarPlanificacion = findViewById(R.id.btn_guardarPlan)
        searchBar = findViewById(R.id.searchViewPicto)

        listaPictogramas = ArrayList()
        listaPlanificacion = ArrayList()
        listaBusqueda = ArrayList()
        //labelBuscando = findViewById(R.id.lbl_buscando)


        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getPictogramas(query)
                listaBusqueda.clear()
                val container = supportFragmentManager.findFragmentById(R.id.contenedor_fragments)
                container?.let {
                    supportFragmentManager.beginTransaction()
                        .remove(it)
                        .commit()
                }
                hideKeyboard()
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
            listaPlanificacion = (intent.getSerializableExtra("pictogramas") as ArrayList<Pictograma>?)!! //TODO
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
                Toast.makeText(
                    applicationContext,
                    "Necesita añadir un título y pictogramas",
                    Toast.LENGTH_LONG
                ).show()
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
                    Toast.makeText(
                        applicationContext,
                        "Planificación " + txt_TituloPlan.text.toString() + " actualizada",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                    val idUsuario = prefs.getString("idUsuario", "")

                    val plan = Planificacion()
                    val creada = idUsuario?.let { it1 ->
                        plan.crearPlanificacion(
                            it1,
                            this@CrearPlanActivity,
                            listaPlanificacion,
                            txt_TituloPlan.text.toString().uppercase(Locale.getDefault())
                        )
                    }
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
            recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            Log.d("TAG",  "se toca el pictograma")
            val position = viewHolder.absoluteAdapterPosition
            listaPlanificacion.removeAt(position)
            recyclerView.adapter!!.notifyItemRemoved(position)
        }
    }

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
                        // R.id.option_2 -> {
                        //     val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                        //     val isPlanificadorLogged = prefs.getBoolean("PlanificadorLogged", false)
                        //     if(isPlanificadorLogged){
                        //         val editor = prefs.edit()
                        //         editor.putBoolean("PlanificadorLogged", false)
                        //         editor.commit()
                        //         val plan = Intent(applicationContext, PlanActivity::class.java)
                        //         startActivity(plan)
                        //     }else{
                        //         crearDialogoLogin()
                        //     }
                        //     true
                        // }
                        R.id.option_3 -> {
                            val dialogLogout = Dialog(this)
                            dialogLogout.setContentView(R.layout.dialogo_logout)
                            dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            btn_logout = dialogLogout.findViewById(R.id.btn_logout)
                            icono_cerrar_login = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
                            btn_logout.setOnClickListener {
                                val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                                val editor = prefs.edit()
                                editor.putBoolean("userAccount", false)
                                editor.apply()
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
            android.R.id.home -> finish()
        }
        return true
    }

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
            listaPictogramas = picto.obtenerFavoritos(this, idUsuario) as ArrayList<Pictograma>
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
        for (pictograma in listaPictogramas) {
            Log.d("asf", "listaPictogramas: ${pictograma.favorito}")
        }

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

                //Añadir pictograma
                picto.nuevoPictograma(
                    this@CrearPlanActivity,
                    titulo_Dialogo.text.toString().uppercase(Locale.getDefault()),
                    ruta,
                    spinner_Dialogo.selectedItem.toString()
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
        adaptador!!.notifyDataSetChanged()
    }

}
