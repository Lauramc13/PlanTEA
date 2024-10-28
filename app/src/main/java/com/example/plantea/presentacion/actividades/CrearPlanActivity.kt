package com.example.plantea.presentacion.actividades

import android.app.Dialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramaEntretenimiento
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
    private var dialogEntretenimiento: Dialog? = null
    private var dialogGuardar: Dialog? = null
    //RecyclerView Planificacion
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_plan)

        btnGuardarPlanificacion = findViewById(R.id.btn_guardarPlan)
        searchBar = findViewById(R.id.searchViewPicto)
        backButton = findViewById(R.id.goBackButton)

        backButton.setOnClickListener{
            dialogEntretenimiento?.dismiss()
            finish()
        }

        observers()

        // Para el dialogo de crear nuevo pictograma
        AniadirPictoUtils.createPickMedia(viewModel, this)

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.setIdUsuario(prefs)

        val callback = viewModel.callBackActivity(this)
        onBackPressedDispatcher.addCallback(this, callback)

        // SearchView para buscar pictogramas
        AniadirPictoUtils.inializeSearch(searchBar, false, viewModel, this@CrearPlanActivity)

        val btnSearch = findViewById<MaterialButton>(R.id.btn_search)

        btnSearch.setOnClickListener {
            if (!CommonUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, R.string.toast_sin_conexion, Toast.LENGTH_SHORT).show()
                searchBar.setQuery("", false)
                searchBar.clearFocus()
            } else {
                if(searchBar.query.trim().isEmpty()){
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
        viewModel.adaptadorPlanificacion = AdaptadorPlanificacion(viewModel.listaPlanificacion, viewModel)
        recyclerView.adapter = viewModel.adaptadorPlanificacion

        //Desplaza la lista para insertar un nuevo pictograma
        recyclerView.scrollToPosition(viewModel.adaptadorPlanificacion.itemCount - 2)
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
          //  viewModel.imagenPicto = it.imagen!! TODO uncomment
            viewModel.categoriaPicto = it.categoria
            viewModel.adaptadorPlanificacion.notifyItemInserted(viewModel.listaPlanificacion.size - 1)
            recyclerView.scrollToPosition(viewModel.adaptadorPlanificacion.itemCount -2)
        }

        viewModel._nuevoPictoDialog.observe(this) {
            if (it) {
                AniadirPictoUtils.initializeDialog(viewModel, this, false)
            }
        }

        viewModel._nuevoPictoDialogCategoria.observe(this) {
            if (it) {
                AniadirPictoUtils.initializeDialog(viewModel, this, true)
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
                    Toast.makeText(this, R.string.toast_campo_vacio, Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.listaPlanificacion[it].historia = historiaText.editText?.text.toString()
                    viewModel.adaptadorPlanificacion.notifyItemChanged(it)
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

            val picker = viewModel.createReloj24(duracionArray[0].toInt(), duracionArray[1].toInt(), this)
            picker.addOnPositiveButtonClickListener {
                val hora = if (picker.hour < 10) "0" + picker.hour else picker.hour
                val min = if (picker.minute < 10) "0" + picker.minute else picker.minute

                viewModel.listaPlanificacion[position].duracion = "$hora:$min"
            }
            picker.show(supportFragmentManager, picker.toString())
        }

        // Open dialog to select pictograma for entretenimiento
        viewModel._onEntretenimientoClicked.observe(this) { position->
            dialogEntretenimiento = Dialog(this)
            dialogEntretenimiento!!.setContentView(R.layout.dialogo_aniadir_actividad)
            dialogEntretenimiento!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val recyclerActividad = dialogEntretenimiento!!.findViewById<RecyclerView>(R.id.recycler_actividad)
            val recyclerEntretenimiento = dialogEntretenimiento!!.findViewById<RecyclerView>(R.id.recycler_entretenimiento)
            val btnClose = dialogEntretenimiento!!.findViewById<ImageView>(R.id.icono_CerrarDialogo)
            val pictograma = Pictograma()
            val listaPictogramas = ArrayList<Pictograma>()

            val idPictoEntretenimiento = viewModel.listaPlanificacion[position].pictoEntretenimiento

            val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
            if(prefs.getBoolean("info_objeto", false)) {
                pictograma.id = "-1"
                pictograma.titulo = prefs.getString("nombreObjeto", "default")!!.uppercase()
                //pictograma.imagen = prefs.getString("imagenObjeto", "default") TODO uncomment
                listaPictogramas.add(pictograma)
                recyclerActividad(recyclerActividad, dialogEntretenimiento!!, listaPictogramas, idPictoEntretenimiento)
            }else{
               val title = dialogEntretenimiento!!.findViewById<TextView>(R.id.txt_actividad)
                title.visibility = View.GONE
                recyclerActividad.visibility = View.GONE
            }

            val language = Locale.getDefault().language
            val pictoEntretenimiento =  pictograma.obtenerPictogramas(this, 4, viewModel.idUsuario, language) as ArrayList<Pictograma>
            recyclerActividad(recyclerEntretenimiento, dialogEntretenimiento!!, pictoEntretenimiento, idPictoEntretenimiento)

            btnClose.setOnClickListener {
                dialogEntretenimiento!!.dismiss()
            }

            dialogEntretenimiento!!.show()
        }

        viewModel._idPictoEntretenimiento.observe(this){
            viewModel.listaPlanificacion[viewModel._onEntretenimientoClicked.value!!].pictoEntretenimiento = it
            Thread.sleep(150)
            dialogEntretenimiento?.dismiss()
        }

    }

    private fun recyclerActividad(recyclerActividad : RecyclerView, dialog: Dialog, listaPictogramas: ArrayList<Pictograma>, idPicto: Int){
        val constraintLayout = dialog.findViewById<ConstraintLayout>(R.id.frameLayout)
        CommonUtils.getGridValueCuaderno(findViewById(android.R.id.content), this, recyclerActividad, constraintLayout, 150, 200)
        val adaptador = AdaptadorPictogramaEntretenimiento(listaPictogramas, idPicto, viewModel)
        recyclerActividad.adapter = adaptador
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
                var listaTitulos = viewModel.planificacion.obtenerTitulosPlanificaciones(viewModel.idUsuario, this)

                //Si la opcionEditar es FALSE se crea una planificación nueva si por el contrario es TRUE se realiza la función editar
                if(txtTituloPlan.editText?.text.toString() == ""){
                    txtTituloPlan.editText?.setText(searchLastTitle(getString(R.string.str_notitle), listaTitulos))
                }
                var idPlan: Int
                val title = searchLastTitle(txtTituloPlan.editText?.text.toString().uppercase(Locale.getDefault()), listaTitulos)

                if (viewModel.opcionEditar) {
                    idPlan = intent.getIntExtra("idPlan", 0)

                    viewModel.planificacion.actualizarPlanificacion(this@CrearPlanActivity, viewModel.idUsuario, idPlan, title, viewModel.listaPlanificacion)
                    Toast.makeText(this, R.string.toast_plan_actualizado, Toast.LENGTH_SHORT).show()

                } else {
                    idPlan = viewModel.planificacion.crearPlanificacion(this@CrearPlanActivity,  viewModel.idUsuario, title)
                    val creada = viewModel.planificacion.addPictogramasPlan(idPlan, this@CrearPlanActivity, viewModel.listaPlanificacion)

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
        viewModel.pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                val inputStream = this.contentResolver?.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val ruta =  CommonUtils.getPathFromUri(this, uri) // TODO: CAMBIAR
                CommonUtils.guardarImagen(this, ruta, bitmap)
                viewModel._image.value = uri

            } else {
                Toast.makeText(this, R.string.toast_no_imagen_seleccionada, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchLastTitle(title: String, listaTitulos: ArrayList<String>): String{
        var i = 1
        var newTitle = title
        while(listaTitulos.contains(newTitle)){
            newTitle = "$title ($i)"
            i++
        }
        return newTitle

    }
}