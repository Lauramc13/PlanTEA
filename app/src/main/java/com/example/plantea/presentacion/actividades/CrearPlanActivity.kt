package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion
import com.example.plantea.presentacion.fragmentos.CategoriasFragment
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment
import com.example.plantea.presentacion.fragmentos.TraduccionPlanFragment
import com.example.plantea.presentacion.viewModels.CrearPlanViewModel
import com.example.plantea.presentacion.viewModels.TraductorViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.util.Collections
import java.util.Locale

class CrearPlanActivity : AppCompatActivity(){
    private lateinit var transaction: FragmentTransaction
    private lateinit var searchBar: SearchView
    private lateinit var backButton: Button

    //Variables dialogo crear nuevo pictograma
    private lateinit var btnGuardarPlanificacion: Button
    private lateinit var txtTituloPlan: TextInputLayout
    private var dialogGuardar: Dialog? = null

    lateinit var recyclerView: RecyclerView

    var fragment = Fragment()

    private val viewModel by viewModels<CrearPlanViewModel>()
    private val viewModelTraductor by viewModels<TraductorViewModel>()

    override fun onStart() {
        super.onStart()
        CommonUtils.loadLemmatizer(Locale.getDefault().language.lowercase(), this)
    }

    override fun onDestroy() {
        dialogGuardar?.dismiss()
        super.onDestroy()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_plan)

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        btnGuardarPlanificacion = findViewById(R.id.btn_guardarPlan)
        searchBar = findViewById(R.id.searchViewPicto)
        backButton = findViewById(R.id.goBackButton)

        backButton.setOnClickListener{
            finish()
        }

        observers()
        viewModel.createPickMedia(viewModel, this)

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.setIdUsuario(prefs)

        val callback = viewModel.callBackActivity(this)
        onBackPressedDispatcher.addCallback(this, callback)

        // SearchView para buscar pictogramas
        inializeSearch()

        val btnSearch = findViewById<MaterialButton>(R.id.btn_search)

        btnSearch.setOnClickListener {
            if (!CommonUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, R.string.toast_sin_conexion, Toast.LENGTH_SHORT).show()
                searchBar.setQuery("", false)
                searchBar.clearFocus()
            } else {
                if(searchBar.query.trim().isEmpty() || searchBar.query == "."){
                    return@setOnClickListener
                }else{
                    viewModel.getPictogramas(searchBar.query.trim().toString(), false, this)
                }
            }
            CommonUtils.hideKeyboard(this, searchBar)
        }

        val btnTranslate = findViewById<MaterialButton>(R.id.btn_translate)

        btnTranslate.setOnClickListener {
            if (!CommonUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, R.string.toast_sin_conexion, Toast.LENGTH_SHORT).show()
                searchBar.setQuery("", false)
                searchBar.clearFocus()
            } else {
                if(searchBar.query.trim().isEmpty()){
                    return@setOnClickListener
                }else{
                     searchBar.clearFocus()
                     viewModelTraductor.traducirFrase(searchBar.query.trim().toString())
                     transaction = supportFragmentManager.beginTransaction()
                     transaction.replace(R.id.contenedor_fragments, TraduccionPlanFragment())
                     transaction.addToBackStack(null)
                     transaction.commit()
                }
            }
            CommonUtils.hideKeyboard(this, searchBar)
        }

        //Iniciar RecyclerView de planificaciones
        initRecyclerViewPlan()

        //Comprobar si hay parametros en caso de llamada desde editar
        val parametros = this.intent.extras
        if (parametros != null) {
            viewModel.configurarParametros(intent, this)
        }

        if(savedInstanceState == null){
            fragment = CategoriasFragment()
            transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contenedor_fragments, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        //Este método se ejecutará al seleccionar el icono guardar para crear la planificación
        btnGuardarPlanificacion.setOnClickListener {
            clickGuardarPicto()
        }

        viewModel.mdImage.observe(this){
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
        viewModel.adaptadorPlanificacion = AdaptadorPlanificacion(viewModel.listaPlanificacion)
        recyclerView.adapter = viewModel.adaptadorPlanificacion

        //Desplaza la lista para insertar un nuevo pictograma
        recyclerView.scrollToPosition(viewModel.adaptadorPlanificacion.itemCount - 2)
    }

    fun observers() {
        viewModel.seCloseFragment.observe(this) {
            if(it){
                transaction = supportFragmentManager.beginTransaction()
                viewModel.closeFragment(transaction, this)
            }
        }

        viewModel.selistaPictogramas.observe(this) {
            transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contenedor_fragments, CategoriasPictogramasFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        viewModel.seClearBusqueda.observe(this) {
            if(it){
                searchBar.setQuery("", false)
                searchBar.clearFocus()
            }
        }

        viewModel.sePictogramaSeleccionado.observe(this) {
            //initRecyclerViewPlan()
            viewModel.tituloPicto = it.titulo!!
          //  viewModel.imagenPicto = it.imagen!! TODO uncomment
            viewModel.categoriaPicto = it.categoria
            viewModel.adaptadorPlanificacion.notifyItemInserted(viewModel.listaPlanificacion.size - 1)
            recyclerView.scrollToPosition(viewModel.adaptadorPlanificacion.itemCount -2)
        }

        viewModel.seIdPictoEntretenimiento.observe(this){
            viewModel.listaPlanificacion[viewModel.seOnEntretenimientoClicked.value!!].pictoEntretenimiento = it
            Thread.sleep(150)
        }
    }


    private fun clickGuardarPicto(){
        if(viewModel.listaPlanificacion.isEmpty()){
            Toast.makeText(this, R.string.toast_planificacion_vacio, Toast.LENGTH_SHORT).show()
        }else {
            dialogGuardar = Dialog(this)
            dialogGuardar?.setContentView(R.layout.dialog_titulo_plan)
            dialogGuardar?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            txtTituloPlan = dialogGuardar?.findViewById(R.id.txt_nombre)!!
            val btnGuardar = dialogGuardar?.findViewById<Button>(R.id.btn_guardar)
            val cerrarDialgo = dialogGuardar?.findViewById<ImageView>(R.id.icono_CerrarDialogo)

            if(viewModel.opcionEditar){
                txtTituloPlan.editText?.setText(intent.getStringExtra("tituloPlan"))
            }

            cerrarDialgo?.setOnClickListener {
                dialogGuardar?.dismiss()
            }

            btnGuardar?.setOnClickListener {
                val listaTitulos = viewModel.planificacion.obtenerTitulosPlanificaciones(viewModel.idUsuario, this)

                //Si la opcionEditar es FALSE se crea una planificación nueva si por el contrario es TRUE se realiza la función editar
                if(txtTituloPlan.editText?.text.toString() == ""){
                    txtTituloPlan.editText?.setText(viewModel.searchLastTitle(getString(R.string.str_notitle), listaTitulos))
                }
                val idPlan: Int
                val title = viewModel.searchLastTitle(txtTituloPlan.editText?.text.toString().uppercase(Locale.getDefault()), listaTitulos)

                if (viewModel.opcionEditar) {
                    idPlan = intent.getIntExtra("idPlan", 0)

                    viewModel.planificacion.actualizarPlanificacion(this@CrearPlanActivity, viewModel.idUsuario, idPlan, title, viewModel.listaPlanificacion)
                    Toast.makeText(this, R.string.toast_plan_actualizado, Toast.LENGTH_SHORT).show()

                } else {
                    idPlan = viewModel.planificacion.crearPlanificacion(this@CrearPlanActivity,  viewModel.idUsuario, title)
                    val creada = viewModel.planificacion.addPictogramasPlan(idPlan, this@CrearPlanActivity, viewModel.idUsuario, viewModel.listaPlanificacion)

                    if (creada != true) {
                        Toast.makeText(this, R.string.toast_error_crear_planificacion, Toast.LENGTH_SHORT).show()
                    }
                }
                //return to the previous activity with extra data
                val intent = intent
                intent.putExtra("idPlan", idPlan)
                intent.putExtra("isNuevo", viewModel.opcionEditar)
                intent.putExtra("tituloPlan", txtTituloPlan.editText?.text.toString().uppercase(Locale.getDefault()))
                setResult(RESULT_OK, intent)
                finish()
            }

            dialogGuardar?.show()
        }
    }

    private fun createPickMedia() {
        viewModel.pickMediaTraductor = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                val inputStream = this.contentResolver?.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val ruta =  CommonUtils.getPathFromUri(this, uri) // TODO: CAMBIAR
                CommonUtils.guardarImagen(this, ruta, bitmap)
                viewModel.mdImage.value = uri
            } else {
                Toast.makeText(this, R.string.toast_no_imagen_seleccionada, Toast.LENGTH_SHORT).show()
            }
        }

        viewModelTraductor.pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {

                val inputStream = this.contentResolver?.openInputStream(uri)
                viewModelTraductor.bitmap = BitmapFactory.decodeStream(inputStream)
                viewModelTraductor.imageSelected()
            } else {
                Toast.makeText(this, R.string.toast_no_imagen_seleccionada, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun inializeSearch() {
        try {
            searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    if (!CommonUtils.isNetworkAvailable(this@CrearPlanActivity)) {
                        Toast.makeText(this@CrearPlanActivity, R.string.toast_sin_conexion, Toast.LENGTH_SHORT).show()
                        searchBar.setQuery("", false)
                        searchBar.clearFocus()
                    } else {
                        //if there are two or more words, search pictograms
                        if (query.trim().contains(" ")) {
                            searchBar.clearFocus()
                            viewModelTraductor.traducirFrase(searchBar.query.trim().toString())
                            transaction = supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.contenedor_fragments, TraduccionPlanFragment())
                            transaction.addToBackStack(null)
                            transaction.commit()
                        } else{
                            viewModel.getPictogramas(query.trim(), false, this@CrearPlanActivity)
                        }
                    }
                    searchBar.clearFocus()
                    CommonUtils.hideKeyboard(this@CrearPlanActivity, searchBar)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    //after a space and a letter, disable the search button
                    if (newText.trim().contains(" ") && newText.trim().length > 1) {
                        findViewById<MaterialButton>(R.id.btn_search).isEnabled = false
                    } else {
                        findViewById<MaterialButton>(R.id.btn_search).isEnabled = true
                    }
                    return true
                }
            })
        }catch (e: Exception){
            e.printStackTrace()
        }

    }
}