package com.example.plantea.presentacion.actividades

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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


class CrearPlanActivity : AppCompatActivity(){
    private lateinit var transaction: FragmentTransaction

    private lateinit var searchBar: SearchView
    private lateinit var backButton: Button

    //Variables dialogo crear nuevo pictograma
    private lateinit var tituloDialogo: TextInputLayout
    private lateinit var spinnerDialogo: Spinner
    private lateinit var imgPicto: ImageView
    private lateinit var imgCerrar: ImageView
    private lateinit var btnGuardar: Button
    private lateinit var btnGuardarPlanificacion: Button
    private lateinit var txtTituloPlan: TextView

    //RecyclerView Planificacion
    lateinit var recyclerView: RecyclerView
    lateinit var adaptador: AdaptadorPlanificacion

    var fragment = Fragment()

    private val viewModel by viewModels<CrearPlanViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_plan)

        btnGuardarPlanificacion = findViewById(R.id.btn_guardarPlan)
        searchBar = findViewById(R.id.searchViewPicto)
        backButton = findViewById(R.id.goBackButton)
        txtTituloPlan = findViewById(R.id.txt_TituloPlan)

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
                if (!CommonUtils.isNetworkAvailable(this@CrearPlanActivity)) {
                    CommonUtils.showSnackbar(findViewById(android.R.id.content), this@CrearPlanActivity, "No hay conexión a internet")
                    searchBar.setQuery("", false)
                    searchBar.clearFocus()
                }else {
                    getPictogramas(query.trim())
                }
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
            txtTituloPlan.text = intent.getStringExtra("titulo")
            viewModel.listaPlanificacion = (intent.getSerializableExtra("pictogramas") as ArrayList<Pictograma>?)!!
        }

        //Iniciar RecyclerView de planificaciones
        initRecyclerViewPlan()

        if(savedInstanceState == null){
            fragment = CategoriasFragment()
            transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contenedor_fragments, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }/*else{
            fragment =  supportFragmentManager.findFragmentById(R.id.contenedor_fragments)!!
        }
        transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor_fragments, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
*/
        //Este método se ejecutará al seleccionar el icono guardar para crear la planificación
        btnGuardarPlanificacion.setOnClickListener {
            clickGuardarPicto()
        }

        viewModel._image.observe(this){
            viewModel.image?.setImageURI(it)
            viewModel.image?.background = null
        }
        createPickMedia()

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        searchBar.clearFocus()
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
        adaptador = AdaptadorPlanificacion(viewModel.listaPlanificacion, viewModel)
        recyclerView.adapter = adaptador

        //Desplaza la lista para insertar un nuevo pictograma
        recyclerView.scrollToPosition(adaptador.itemCount - 2)
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
            imgPicto.setImageURI(image)
            imgPicto.background = null
        } else {
            CommonUtils.showSnackbar(findViewById(android.R.id.content), this, "No se ha seleccionado ninguna imagen")
        }
    }

    fun observers() {
        viewModel._closeFragment.observe(this) {
            if(it){
                transaction = supportFragmentManager.beginTransaction()
                viewModel.closeFragment(transaction, this)
            }
        }

        viewModel._listaPictogramas.observe(this) {
            transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contenedor_fragments, CategoriasPictogramasFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        viewModel._clearBusqueda.observe(this) {
            if(it){
                searchBar.setQuery("", false)
                searchBar.clearFocus()
            }
        }

        viewModel._pictogramaSeleccionado.observe(this) {
            //initRecyclerViewPlan()
            viewModel.tituloPicto = it.titulo!!
            viewModel.imagenPicto = it.imagen!!
            viewModel.categoriaPicto = it.categoria
            adaptador.notifyItemInserted(viewModel.listaPlanificacion.size - 1)
            recyclerView.scrollToPosition(adaptador.itemCount -2)
        }

        viewModel._nuevoPictoDialog.observe(this) {
            if (it) {
                //cerrarFragment(); //Cerrar fragmento al abrir dialogo
                val dialogNuevoPictograma = Dialog(this)
                dialogNuevoPictograma.setContentView(R.layout.dialogo_nuevo_pictograma)
                dialogNuevoPictograma.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialogNuevoPictograma.show()
                imgCerrar = dialogNuevoPictograma.findViewById(R.id.icono_CerrarDialogo)
                btnGuardar = dialogNuevoPictograma.findViewById(R.id.btn_GuardarPicto)
                imgPicto = dialogNuevoPictograma.findViewById(R.id.img_NuevoPicto)
                tituloDialogo = dialogNuevoPictograma.findViewById(R.id.txt_Titulo)
                spinnerDialogo = dialogNuevoPictograma.findViewById(R.id.spinner_Categorias)
                val categorias = viewModel.categoria.consultarCategorias(this)
               // spinner_Dialogo

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias as ArrayList<String>)
                spinnerDialogo.adapter = adapter
                if(viewModel.subcategoriaOpen){
                    spinnerDialogo.setSelection(viewModel.identificadorSubCategoria-1)
                }else{
                    spinnerDialogo.setSelection(viewModel.identificadorCategoria-1)
                }

                btnGuardar.setOnClickListener {
                    if (tituloDialogo.editText?.text.toString().isEmpty()) {
                        CommonUtils.showSnackbar(findViewById(android.R.id.content), this, "Se necesita un título para el nuevo pictograma")
                    } else if (imgPicto.drawable == null) {
                        CommonUtils.showSnackbar(findViewById(android.R.id.content), this, "Se necesita una imagen para el nuevo pictograma")
                    } else {
                        val imagen = tituloDialogo.editText?.text.toString() //Nombre de la imagen
                        val image = (imgPicto.drawable as BitmapDrawable).bitmap

                        val ruta = viewModel.guardarImagen(applicationContext, imagen, image)
                        CommonUtils.showSnackbar(findViewById(android.R.id.content), this, "Nuevo pictograma creado")
                        dialogNuevoPictograma.dismiss() //Cerrar dialogo

                        //Añadir pictograma
                        val id = viewModel.pictograma.nuevoPictograma(this@CrearPlanActivity, tituloDialogo.editText?.text.toString().uppercase(Locale.getDefault()), ruta, spinnerDialogo.selectedItem.toString(), viewModel.idUsuario)

                        val pictograma = Pictograma()
                        pictograma.id = id
                        pictograma.titulo = tituloDialogo.editText?.text.toString().uppercase(Locale.getDefault())
                        pictograma.imagen = ruta
                        pictograma.categoria = spinnerDialogo.selectedItemPosition + 1
                        viewModel._listaPictogramas.value?.add(pictograma)
                        //find current fragment and update the recycler
                        val categoriasPictoFragment = supportFragmentManager.findFragmentById(R.id.contenedor_fragments) as CategoriasPictogramasFragment

                        categoriasPictoFragment.recyclerPictogramas.adapter?.notifyItemInserted(viewModel._listaPictogramas.value!!.size - 1)

                        viewModel.categoria.crearCategoria(this@CrearPlanActivity, tituloDialogo.editText?.text.toString().uppercase(Locale.getDefault()), ruta, 0,"default", viewModel.idUsuario)
                    }
                }

                imgCerrar.setOnClickListener {
                    dialogNuevoPictograma.dismiss()
                }

                imgPicto.setOnClickListener {
                    abrirGaleria()
                }
            }
        }

        viewModel._historiaClicked.observe(this) {
            val tituloCard = viewModel.listaPlanificacion[it].titulo
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialogo_historiasocial)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val btnGuardar = dialog.findViewById<Button>(R.id.btn_eliminarEvento)
            val cardtitulo = dialog.findViewById<TextView>(R.id.cardName)
            cardtitulo.text = tituloCard
            val iconoCerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)
            val historiaText = dialog.findViewById<TextInputLayout>(R.id.historiaText)

            if (viewModel.listaPlanificacion[it].historia.toString() == "null") {
                historiaText.editText?.setText("")
            } else {
                historiaText.editText?.setText(viewModel.listaPlanificacion[it].historia)
            }

            iconoCerrar.setOnClickListener { dialog.dismiss() }

            btnGuardar.setOnClickListener { view ->
                if (historiaText.editText?.text.toString() == "") {
                    CommonUtils.showSnackbar(view, this, "No puedes dejar el campo vacío")
                } else {
                    viewModel.listaPlanificacion[it].historia = historiaText.editText?.text.toString()
                    adaptador.notifyItemChanged(it)
                    dialog.dismiss()
                }
            }

            dialog.show()
        }

        viewModel._onDuracionClicked.observe(this) {position->
            var duracion = viewModel.listaPlanificacion[position].duracion
            if(duracion == null || duracion == "null"){
                duracion = "00:00"
            }
            val duracionArray = duracion.split(":")

            val picker = viewModel.createReloj24(duracionArray[0].toInt(), duracionArray[1].toInt())
            picker.addOnPositiveButtonClickListener {
                val hora = if (picker.hour < 10) "0" + picker.hour else picker.hour
                val min = if (picker.minute < 10) "0" + picker.minute else picker.minute

                viewModel.listaPlanificacion[position].duracion = "$hora:$min"
            }
            picker.show(supportFragmentManager, picker.toString())
        }
    }

    private fun clickGuardarPicto(){
        if (txtTituloPlan.text.toString().isEmpty()) {
            CommonUtils.showSnackbar(findViewById(android.R.id.content), this, "No puedes dejar el campo de título vacío")
        }else if(viewModel.listaPlanificacion.isEmpty()){
            CommonUtils.showSnackbar(findViewById(android.R.id.content), this, "No puedes dejar la planificación vacía")
        }else {
            //Si la opcionEditar es FALSE se crea una planificación nueva si por el contrario es TRUE se realiza la función editar
            if (viewModel.opcionEditar) {
                viewModel.planificacion.actualizarPlanificacion(this@CrearPlanActivity, intent.getIntExtra("identificador", 0), txtTituloPlan.text.toString().uppercase(Locale.getDefault()), viewModel.listaPlanificacion)
                CommonUtils.showSnackbar(findViewById(android.R.id.content), this, "Planificación " + txtTituloPlan.text.toString() + " actualizada")
            } else {
                val idPlan = viewModel.planificacion.crearPlanificacion(this@CrearPlanActivity,  viewModel.idUsuario,txtTituloPlan.text.toString().uppercase(Locale.getDefault()))
                val creada = viewModel.planificacion.addPictogramasPlan(idPlan, this@CrearPlanActivity, viewModel.listaPlanificacion)

                if (creada == true) {
                    CommonUtils.showSnackbar(findViewById(android.R.id.content), this, "Planificación " + txtTituloPlan.text.toString() + " creada")
                } else {
                    CommonUtils.showSnackbar(findViewById(android.R.id.content), this, "Error al crear la planificación")
                }
            }

            finish()
        }
    }

    fun getPictogramas(query: String) {
        viewModel.busquedaOpen = true
        val pictogramasBusqueda = ArrayList<Pictograma>()
        CoroutineScope(Dispatchers.IO).launch {
            val dict = CommonUtils.getDataApi(query)

            withContext(Dispatchers.Main) {
                dict.keys.mapNotNull { key ->
                    dict[key]?.let { (value, id) ->
                        pictogramasBusqueda.add(viewModel.crearPictoBusqueda(key, value, id, this@CrearPlanActivity))
                    }
                }
            }

            if (pictogramasBusqueda.isNotEmpty()) {
                viewModel._listaPictogramas.postValue(pictogramasBusqueda)
            }
        }

    }


    fun createPickMedia() {
        viewModel.pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                val inputStream = this.contentResolver?.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val ruta =  CommonUtils.getPathFromUri(this, uri)
                CommonUtils.guardarImagen(this, ruta, bitmap)
                viewModel._image.value = uri

            } else {
                CommonUtils.showSnackbar(findViewById(android.R.id.content),this, "No se ha seleccionado ninguna imagen")
            }
        }
    }
}