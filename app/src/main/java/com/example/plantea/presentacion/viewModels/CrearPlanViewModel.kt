package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Categoria
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.AniadirPictoUtils
import com.example.plantea.presentacion.actividades.AniadirPictoUtils.Companion
import com.example.plantea.presentacion.actividades.AniadirPictoUtils.Companion.clearButtonsSelected
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorCategorias
import com.example.plantea.presentacion.adaptadores.AdaptadorNuevoPicto
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramas
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion
import com.example.plantea.presentacion.fragmentos.CategoriasFragment
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class CrearPlanViewModel : ViewModel(), AdaptadorCategorias.OnItemSelectedListener, AniadirPictoUtils.Companion.CustomViewModel {

    override val pictograma: Pictograma = Pictograma()
    override var idUsuario: String = ""
    override var _listaPictoRandom: SingleLiveEvent<ArrayList<Pictograma>> = SingleLiveEvent()
    override lateinit var adaptadorRandomPictos: AdaptadorNuevoPicto
    override var _listaPictogramas: SingleLiveEvent<ArrayList<Pictograma>> = SingleLiveEvent()
    override var _imageSelected = SingleLiveEvent<Bitmap>()
    override lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    override var _nuevoPicto: SingleLiveEvent<Pictograma?> = SingleLiveEvent()
    override var saltar = false
    override var isEditImage = false
    override var isCalendarioMensual = false


    var identificadorCategoria : Int = -1
    var _closeFragment = SingleLiveEvent<Boolean>()
    var _clearBusqueda = SingleLiveEvent<Boolean>()
    val _pictogramaSeleccionado = SingleLiveEvent<Pictograma>()
    var _onEntretenimientoClicked = SingleLiveEvent<Int>()
    var _idPictoEntretenimiento = SingleLiveEvent<Int>()
    val _image = MutableLiveData<Uri?>()
    var image : ImageView? = null
    lateinit var pickMediaTraductor: ActivityResultLauncher<PickVisualMediaRequest>

    lateinit var adaptadorPlanificacion :  AdaptadorPlanificacion
    lateinit var adaptador: AdaptadorNuevoPicto //Adaptador del recyclerview del dia
    lateinit var adaptadorCategoriaPictograma : AdaptadorPictogramas

    //var subcategoriaOpen = false
    var busquedaOpen = false
    var categoriaPicto = 0
    lateinit var tituloPicto: String
    var isEdited = false
    var categoria = Categoria()
    val planificacion = Planificacion()
    var _createdCategoria = SingleLiveEvent<Boolean>()
    var _deletedCategoria = SingleLiveEvent<Int>()
    var listaCategorias = ArrayList<Categoria>()

    @SuppressLint("StaticFieldLeak")
    var activity: Activity = Activity()
    var listaPlanificacion =  ArrayList<Pictograma>()

    //Opcion para indicar funcionalidad editar o crear
    var opcionEditar = false
    var isCrearCategoria = false

    fun setIdUsuario(prefs: android.content.SharedPreferences) {
        idUsuario = prefs.getString("idUsuario", "").toString()
    }

    fun pictogramaSeleccionado(posicion: Int) {
        _listaPictogramas.value?.let { listaPlanificacion.add(it[posicion].copy()) }
        isEdited = true
        _pictogramaSeleccionado.value = _listaPictogramas.value?.get(posicion)?.copy()
    }

    fun nuevoPictogramaDialogo(activity: Activity){
        AniadirPictoUtils.initializeDialog(this, activity)
    }

    fun callBackActivity(activity: Activity): OnBackPressedCallback {
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity.finish()
            }
        }
        return callback
    }

    fun closeFragment(transaction: FragmentTransaction, context: Context){ //ESTO HAY QUE MEJORARLO
        if(busquedaOpen){
            transaction.replace(R.id.contenedor_fragments, CategoriasFragment())
            transaction.addToBackStack(null)
            transaction.commit()
            busquedaOpen = false
        }else{
            /*if(subcategoriaOpen){
                _listaPictogramas.value = pictograma.obtenerPictogramas(context, identificadorCategoria, idUsuario, Locale.getDefault().language)  as ArrayList<Pictograma>
                subcategoriaOpen = false
            }else{*/
                transaction.replace(R.id.contenedor_fragments, CategoriasFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            //}
        }
        _clearBusqueda.value = true
    }

    override fun onItemSeleccionado(categoria: Int, context: Context) {
        if(categoria == 5){
            _listaPictogramas.value = pictograma.obtenerFavoritos(context, idUsuario, Locale.getDefault().language)
            CoroutineScope(Dispatchers.Main).launch {
                _listaPictogramas.value!!.forEach { pictogram ->
                    if (pictogram.idAPI != 0) {
                        pictogram.imagen = BitmapFactory.decodeResource(context.resources, R.drawable.loading_placeholder)
                        pictogram.imagen = withContext(Dispatchers.IO) {
                            CommonUtils.getImagenAPI(pictogram.idAPI)
                        }
                    }
                    adaptadorCategoriaPictograma.notifyItemChanged(_listaPictogramas.value!!.indexOf(pictogram))

                }
            }
        }else{
            val listaPictogramas = pictograma.obtenerPictogramas(context, categoria, idUsuario, Locale.getDefault().language) as ArrayList<Pictograma>

            CoroutineScope(Dispatchers.Main).launch {
                listaPictogramas.forEach { pictogram ->
                    if (pictogram.idAPI != 0) {
                        pictogram.imagen = BitmapFactory.decodeResource(context.resources, R.drawable.loading_placeholder)
                        pictogram.imagen = withContext(Dispatchers.IO) {
                            CommonUtils.getImagenAPI(pictogram.idAPI)
                        }
                    }
                }
                _listaPictogramas.value = listaPictogramas
            }

        }
        identificadorCategoria = categoria
    }

    override fun addCategoria(view: View?, activity: Activity){
        isCrearCategoria = true
        AniadirPictoUtils.initializeDialog(this, activity)
    }

    override fun borrarCategoria(posicion: Int, categoria: Int, view: View?){
        //dialog
        val dialogo = Dialog(view!!.context)
        dialogo.setContentView(R.layout.dialogo_borrar_categoria)
        dialogo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnBorrar : Button = dialogo.findViewById(R.id.btn_eliminarCategoria)
        val iconoCerrarLogin : ImageView = dialogo.findViewById(R.id.icono_CerrarDialogo)

        btnBorrar.setOnClickListener{
            this.categoria.eliminarCategoria(view.context as Activity, idUsuario, categoria)
            listaCategorias.removeAt(posicion)
            _deletedCategoria.value = posicion
            dialogo.dismiss()
        }

        iconoCerrarLogin.setOnClickListener { dialogo.dismiss() }

        dialogo.show()
    }


  /*  fun createReloj24(hora: Int, minutos: Int, context: Context): MaterialTimePicker {
        return MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hora)
            .setMinute(minutos)
            .setTheme(R.style.TimePicker)
            .setTitleText(getString(context, R.string.selecciona_hora))
            .build()
    }*/

    fun configurarParametros(intent: Intent, context: Context) {
        opcionEditar = true
        listaPlanificacion = (intent.getSerializableExtra("pictogramas") as ArrayList<Pictograma>?)!!

        listaPlanificacion.forEachIndexed { index, pictogram ->
            listaPlanificacion[index].imagen = CommonUtils.byteArrayToBitmap(intent.getByteArrayExtra("imagen_$index"))
            pictogram.imagen = CommonUtils.byteArrayToBitmap(intent.getByteArrayExtra("imagen_$index"))

            if (pictogram.idAPI != 0) {
                pictogram.imagen = BitmapFactory.decodeResource(context.resources, R.drawable.loading_placeholder)
            }

            CoroutineScope(Dispatchers.Main).launch {
                listaPlanificacion.forEach { pictogram ->
                    if (pictogram.idAPI != 0) {
                        pictogram.imagen = withContext(Dispatchers.IO) {
                            CommonUtils.getImagenAPI(pictogram.idAPI)
                        }
                    }
                }
                adaptadorPlanificacion.updateListaPlan(listaPlanificacion)
            }

        }
    }

    fun searchLastTitle(title: String, listaTitulos: ArrayList<String>): String{
        var i = 1
        var newTitle = title
        while(listaTitulos.contains(newTitle)){
            newTitle = "$title ($i)"
            i++
        }
        return newTitle
    }

    //Metodos de la interfaz de AniadirPictoUtils
    override val onItemSelectedListener = object : AdaptadorNuevoPicto.OnItemSelectedListener {
        override fun onNuevoPicto(pictogram: Pictograma?) {
            _nuevoPicto.value = pictogram
        }
    }

    override fun setupTitle(titleDialog: TextView, activity: Activity) {
        if(isCrearCategoria){
            titleDialog.text = activity.getString(R.string.crear_categoria).uppercase()
        }else{
            titleDialog.text = activity.getString(R.string.crear_pictograma).uppercase()
        }
    }

    override fun dialogoAniadirPicto(dialogo: Dialog, view: ViewGroup, activity: Activity, buttons: LinearLayout, btnSiguiente: MaterialButton, btnSaltar: MaterialButton) {
        if(isCrearCategoria){
            dialogCategoria(dialogo, view, activity, buttons, btnSiguiente, btnSaltar)
        }else{
            dialogPictograma(dialogo, view, activity, buttons, btnSiguiente, btnSaltar)
        }
    }

    private fun dialogCategoria(dialogo: Dialog, view: ViewGroup, activity: Activity, buttons: LinearLayout, btnSiguiente: MaterialButton, btnSaltar: MaterialButton){
        val lastView = view.getChildAt(view.childCount - 1)
        view.removeAllViews()
        view.addView(activity.layoutInflater.inflate(R.layout.dialogo_crear_categoria_plan, null))
        val imagenPicto = view.findViewById<ImageView>(R.id.img_NuevoPicto)
        imagenPicto.setImageBitmap(_nuevoPicto.value?.imagen)

        imagenPicto.setOnClickListener {
            buttons.visibility = View.VISIBLE
            btnSiguiente.visibility = View.VISIBLE
            adaptadorRandomPictos.currentPosition = RecyclerView.NO_POSITION
            adaptadorRandomPictos.notifyDataSetChanged()

            view.removeAllViews()
            view.addView(lastView)
        }

        val nombrePictograma = view.findViewById<TextInputLayout>(R.id.txt_Titulo)
        var colorSelected = "default"

        val buttonGuardar = view.findViewById<Button>(R.id.btn_GuardarPicto)

        //Colores buttons
        val buttonMorado = dialogo.findViewById<MaterialButton>(R.id.fab1)
        val buttonRosa = dialogo.findViewById<MaterialButton>(R.id.fab2)
        val buttonVerde = dialogo.findViewById<MaterialButton>(R.id.fab3)
        val buttonAmarillo = dialogo.findViewById<MaterialButton>(R.id.fab4)
        val buttonAzul = dialogo.findViewById<MaterialButton>(R.id.fab5)
        val buttonDefault = dialogo.findViewById<MaterialButton>(R.id.fab6)

        buttonAmarillo.setOnClickListener {
            clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
            buttonAmarillo.icon = ContextCompat.getDrawable(activity,R.drawable.svg_check)
            colorSelected = "yellow"
        }

        buttonAzul.setOnClickListener {
            clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
            buttonAzul.icon = ContextCompat.getDrawable(activity,R.drawable.svg_check)
            colorSelected = "blue"
        }

        buttonMorado.setOnClickListener {
            clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
            buttonMorado.icon = ContextCompat.getDrawable(activity,R.drawable.svg_check)
            colorSelected = "purple"
        }

        buttonRosa.setOnClickListener {
            clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
            buttonRosa.icon = ContextCompat.getDrawable(activity,R.drawable.svg_check)
            colorSelected = "pink"
        }

        buttonVerde.setOnClickListener {
            clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
            buttonVerde.icon = ContextCompat.getDrawable(activity,R.drawable.svg_check)
            colorSelected = "green"
        }

        buttonDefault.setOnClickListener {
            clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
            buttonDefault.icon = ContextCompat.getDrawable(activity,R.drawable.svg_check)
            colorSelected = "default"
        }

        val categoria = Categoria()
        buttonGuardar.setOnClickListener {
            if (nombrePictograma.editText?.text.toString().isEmpty()) {
                nombrePictograma.error = activity.getString(R.string.toast_obligatorio)
            } else if (categoria.checkCategoriaExiste(view.context, nombrePictograma.editText?.text.toString(), idUsuario, Locale.getDefault().language)) {
                Toast.makeText(view.context, R.string.toast_categoria_existente, Toast.LENGTH_SHORT).show()
            } else {
                val imagenBlob = CommonUtils.bitmapToByteArray((imagenPicto.drawable as BitmapDrawable).bitmap)
                val idCategoria = categoria.crearCategoria(activity, nombrePictograma.editText?.text.toString().uppercase(), imagenBlob, colorSelected, idUsuario)

                val newCategoria = Categoria(idCategoria, nombrePictograma.editText?.text.toString().uppercase(), (imagenPicto.drawable as BitmapDrawable).bitmap, colorSelected)
                listaCategorias.add(listaCategorias.size - 1, newCategoria)
                _createdCategoria.value = true

                dialogo.dismiss() //Cerrar dialogo
            }
        }
    }

    private fun dialogPictograma(dialogo: Dialog, view: ViewGroup, activity: Activity, buttons: LinearLayout, btnSiguiente: MaterialButton, btnSaltar: MaterialButton){
        val lastView = view.getChildAt(view.childCount - 1)
        view.removeAllViews()
        view.addView(activity.layoutInflater.inflate(R.layout.fragment_nuevo_picto_nombre, null))
        val imagenPicto = view.findViewById<ImageView>(R.id.img_NuevoPicto)
        imagenPicto.setImageBitmap(_nuevoPicto.value?.imagen)

        imagenPicto.setOnClickListener {
            buttons.visibility = View.VISIBLE
            btnSiguiente.visibility = View.VISIBLE
            adaptadorRandomPictos.currentPosition = RecyclerView.NO_POSITION
            adaptadorRandomPictos.notifyDataSetChanged()
            view.removeAllViews()
            view.addView(lastView)
        }

        val categorias = categoria.obtenerCategoriasPrincipales(activity, idUsuario, Locale.getDefault().language)
        val spinnerDialogo = view.findViewById<Spinner>(R.id.spinner_Categorias)
        val nombrePictograma = view.findViewById<TextInputLayout>(R.id.txt_Titulo)

        spinnerDialogo.adapter = ArrayAdapter(activity.applicationContext, android.R.layout.simple_spinner_dropdown_item, categorias.map { it.getTitulo() })

        //find position of the category by the identificador
        spinnerDialogo.setSelection(findId(identificadorCategoria, categorias))

        val buttonGuardar = view.findViewById<Button>(R.id.btn_GuardarPicto)

        buttonGuardar.setOnClickListener {
            if (nombrePictograma.editText?.text.toString().isEmpty()) {
                Toast.makeText(activity, R.string.toast_necesita_titulo, Toast.LENGTH_SHORT).show()
            } else if (imagenPicto.drawable == null) {
                Toast.makeText(activity, R.string.toast_necesita_imagen, Toast.LENGTH_SHORT).show()
            } else {
                val imageBlob = CommonUtils.bitmapToByteArray((imagenPicto.drawable as BitmapDrawable).bitmap)
                Toast.makeText(activity, R.string.toast_pictograma_creado, Toast.LENGTH_SHORT).show()

                dialogo.dismiss()

                //Añadir pictograma
                var idCategoria = categoria.obtenerCategoria(activity, spinnerDialogo.selectedItem.toString(), Locale.getDefault().language)
                //if categoria is between 0 and 3
                if(idCategoria in 1..4){
                    idCategoria = categoria.duplicateCategoria(activity.applicationContext, idUsuario, idCategoria)
                }
                val id = if(_nuevoPicto.value?.idAPI != 0){
                    pictograma.nuevoPictogramaAPI(activity, nombrePictograma.editText?.text.toString().uppercase(Locale.getDefault()), _nuevoPicto.value?.idAPI.toString(), idCategoria.toString())
                }else{
                    pictograma.nuevoPictogramaLocal(activity, nombrePictograma.editText?.text.toString().uppercase(Locale.getDefault()), imageBlob, idCategoria.toString(), idUsuario)
                }

                val pictograma = Pictograma()
                pictograma.titulo = nombrePictograma.editText?.text.toString().uppercase(Locale.getDefault())
                pictograma.categoria = idCategoria
                pictograma.id = id
                pictograma.imagen = (imagenPicto.drawable as BitmapDrawable).bitmap
                _listaPictogramas.value?.add(pictograma)

                //find current fragment and update the recycler
                val categoriasPictoFragment =
                    (activity as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.contenedor_fragments) as CategoriasPictogramasFragment
                categoriasPictoFragment.recyclerPictogramas.adapter?.notifyItemInserted(
                    _listaPictogramas.value!!.size - 1
                )
                categoriasPictoFragment.textoVacio.visibility = View.GONE
            }
        }
    }

    private fun findId(id: Int, categorias: ArrayList<Categoria>): Int {
        var position = 0
        for (i in categorias.indices) {
            if (categorias[i].getCategoria() == id) {
                position = i
                break
            }
        }
        return position

    }

    override fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}