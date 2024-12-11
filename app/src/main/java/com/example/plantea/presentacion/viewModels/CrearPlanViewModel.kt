package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Categoria
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorCategorias
import com.example.plantea.presentacion.adaptadores.AdaptadorNuevoPicto
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramaEntretenimiento
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramas
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion
import com.example.plantea.presentacion.fragmentos.CategoriasFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class CrearPlanViewModel : ViewModel(), AdaptadorCategorias.OnItemSelectedListener, AdaptadorNuevoPicto.OnItemSelectedListener {

    var identificadorCategoria : Int = -1
    var _closeFragment = SingleLiveEvent<Boolean>()
    var _clearBusqueda = SingleLiveEvent<Boolean>()
    val _pictogramaSeleccionado = SingleLiveEvent<Pictograma>()
    val _nuevoPictoDialog = SingleLiveEvent<Boolean>()
    val _nuevoPictoDialogCategoria = SingleLiveEvent<Boolean>()
    var _historiaClicked = SingleLiveEvent<Int>()
    var _onDuracionClicked = SingleLiveEvent<Int>()
    var _onEntretenimientoClicked = SingleLiveEvent<Int>()
    var _idPictoEntretenimiento = SingleLiveEvent<Int>()
    var _nuevoPicto = SingleLiveEvent<Pictograma?>()
    var _listaPictoRandom = SingleLiveEvent<ArrayList<Pictograma>>()
    val _image = MutableLiveData<Uri?>()
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    var image : ImageView? = null

    lateinit var adaptadorPlanificacion :  AdaptadorPlanificacion

    lateinit var adaptador: AdaptadorNuevoPicto //Adaptador del recyclerview del dia

    lateinit var adaptadorCategoriaPictograma : AdaptadorPictogramas

    //var subcategoriaOpen = false
    var busquedaOpen = false
    var categoriaPicto = 0
    lateinit var tituloPicto: String
    lateinit var imagenPicto: String
    var isEdited = false
    var pictograma = Pictograma()
    var categoria = Categoria()
    val planificacion = Planificacion()
    var _createdCategoria = SingleLiveEvent<Boolean>()
    var _deletedCategoria = SingleLiveEvent<Int>()
    var listaCategorias = ArrayList<Categoria>()

    @SuppressLint("StaticFieldLeak")
    var activity: Activity = Activity()
    var idUsuario = "0"

    var listaPlanificacion =  ArrayList<Pictograma>()
    var _listaPictogramas  = SingleLiveEvent<ArrayList<Pictograma>>()

    //Opcion para indicar funcionalidad editar o crear
    var opcionEditar = false

    fun setIdUsuario(prefs: android.content.SharedPreferences) {
        idUsuario = prefs.getString("idUsuario", "").toString()
    }

    fun pictogramaSeleccionado(posicion: Int) {
        _listaPictogramas.value?.let { listaPlanificacion.add(it[posicion].copy()) }
        isEdited = true
        _pictogramaSeleccionado.value = _listaPictogramas.value?.get(posicion)?.copy()
    }

    fun nuevoPictogramaDialogo(){
        _nuevoPictoDialog.value = true
    }

    private fun crearPictoBusqueda(bitmap: Bitmap, titulo: String?, id: Int, activity: Activity): Pictograma {
        val favorito = pictograma.getFavorito(activity, id.toString(), idUsuario)
        return Pictograma(id.toString(), titulo?.uppercase(), bitmap, id, 0, favorito)
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

    /*override fun onMenuClick(position: Int, view: View, context: Context){
       val inflater = LayoutInflater.from(context)
        val customView = inflater.inflate(R.layout.popup_menu_crear_plan, null)
        val popupWindow = PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        customView.findViewById<TextView>(R.id.item_historia).setOnClickListener {
            _historiaClicked.value = position
            popupWindow.dismiss()
        }

        customView.findViewById<TextView>(R.id.item_duracion).setOnClickListener {
            _onDuracionClicked.value = position
            popupWindow.dismiss()
        }

        customView.findViewById<TextView>(R.id.item_entretenimiento).setOnClickListener {
            _onEntretenimientoClicked.value = position
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(view)
    }*/

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

    override fun addCategoria(view: View?){
        _nuevoPictoDialogCategoria.value = true
    }


    override fun onNuevoPicto(picto: Pictograma?) {
        _nuevoPicto.value = picto
    }

    fun getPictogramas(query: String, isNuevoPictoBusqueda: Boolean, activity: Activity) {
        busquedaOpen = true
        val pictogramasBusqueda = ArrayList<Pictograma>()
        CoroutineScope(Dispatchers.IO).launch {
            val dict = CommonUtils.getDataApi(query)

            withContext(Dispatchers.Main) {
                dict.keys.mapNotNull { key ->
                    dict[key]?.let { (_, id) ->
                        pictogramasBusqueda.add(crearPictoBusqueda(key, query, id, activity))
                    }
                }
            }

            if (pictogramasBusqueda.isNotEmpty()) {
                if(isNuevoPictoBusqueda){
                    _listaPictoRandom.postValue(pictogramasBusqueda)
                } else{
                    _listaPictogramas.postValue(pictogramasBusqueda)
                }

            }
        }
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
}