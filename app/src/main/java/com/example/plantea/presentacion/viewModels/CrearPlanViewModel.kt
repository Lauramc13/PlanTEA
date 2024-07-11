package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion
import com.example.plantea.presentacion.adaptadores.AdaptadorPresentacion
import com.example.plantea.presentacion.fragmentos.CategoriasFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

class CrearPlanViewModel : ViewModel(), AdaptadorPlanificacion.OnItemSelectedListener, AdaptadorCategorias.OnItemSelectedListener, AdaptadorPictogramaEntretenimiento.OnItemSelectedListener, AdaptadorNuevoPicto.OnItemSelectedListener {

    var identificadorCategoria : Int = -1
    var identificadorSubCategoria : Int = -1
    var _closeFragment = SingleLiveEvent<Boolean>()
    var _clearBusqueda = SingleLiveEvent<Boolean>()
    val _pictogramaSeleccionado = SingleLiveEvent<Pictograma>()
    val _nuevoPictoDialog = SingleLiveEvent<Boolean>()
    val _nuevoPictoDialogCategoria = SingleLiveEvent<Boolean>()
    var _historiaClicked = SingleLiveEvent<Int>()
    var _onDuracionClicked = SingleLiveEvent<Int>()
    var _onEntretenimientoClicked = SingleLiveEvent<Int>()
    var _idPictoEntretenimiento = SingleLiveEvent<Int>()
    var _imagenNuevoPicto = SingleLiveEvent<String?>()
    var _listaPictoRandom = SingleLiveEvent<ArrayList<Pictograma>>()
    val _image = MutableLiveData<Uri?>()
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    var image : ImageView? = null

    lateinit var adaptador: AdaptadorNuevoPicto //Adaptador del recyclerview del dia

    var subcategoriaOpen = false
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


    //Método para mostrar los pictogramas correspondientes a las categorias de consultas
    fun mostrarsubCategoria(tituloCategoria: String?, context: Context) {
        //labelBuscando.visibility = View.GONE
        val categoria = categoria.obtenerCategoria(context, tituloCategoria, Locale.getDefault().language)
        subcategoriaOpen = true
        val language = Locale.getDefault().language
        _listaPictogramas.value = pictograma.obtenerPictogramas(context, categoria, idUsuario, language) as ArrayList<Pictograma>
        identificadorSubCategoria = categoria
    }

    fun pictogramaSeleccionado(posicion: Int) {
        _listaPictogramas.value?.let { listaPlanificacion.add(it[posicion].copy()) }
        isEdited = true
        _pictogramaSeleccionado.value = _listaPictogramas.value?.get(posicion)?.copy()
    }

    fun nuevoPictogramaDialogo(){
        _nuevoPictoDialog.value = true
    }

    fun crearPictoBusqueda(bitmap: Bitmap, titulo: String?, id: Int, activity: Activity): Pictograma {
        val tituloMayus = titulo?.uppercase()
        val favorito = pictograma.getFavorito(activity, id.toString(), idUsuario)
        val archivo = CommonUtils.crearImagen(bitmap, titulo, activity)
        return Pictograma(id.toString(), tituloMayus, archivo, 0, 0, favorito, true)
    }

    fun callBackActivity(activity: Activity): OnBackPressedCallback {
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity.finish()
            }
        }
        return callback
    }

    fun guardarImagen(context: Context, nombre: String, imagen: Bitmap): String {
        val cw = ContextWrapper(context)
        val dirImages = cw.getDir("Imagenes", AppCompatActivity.MODE_PRIVATE)
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

    fun closeFragment(transaction: FragmentTransaction, context: Context){ //ESTO HAY QUE MEJORARLO
        if(busquedaOpen){
            transaction.replace(R.id.contenedor_fragments, CategoriasFragment())
            transaction.addToBackStack(null)
            transaction.commit()
            busquedaOpen = false
        }else{
            if(subcategoriaOpen){
                _listaPictogramas.value = pictograma.obtenerPictogramas(context, identificadorCategoria, idUsuario, Locale.getDefault().language)  as ArrayList<Pictograma>
                subcategoriaOpen = false
            }else{
                transaction.replace(R.id.contenedor_fragments, CategoriasFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        _clearBusqueda.value = true
    }

    override fun onMenuClick(position: Int, anchorView: View, context: Context){
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

        popupWindow.showAsDropDown(anchorView)
    }

    override fun onItemSeleccionado(idCategoria: Int, context: Context) {
        _listaPictogramas.value = if (idCategoria == 5) {
            pictograma.obtenerFavoritos(context, idUsuario, Locale.getDefault().language)
        } else {
            pictograma.obtenerPictogramas(context, idCategoria, idUsuario, Locale.getDefault().language) as ArrayList<Pictograma>
        }
        identificadorCategoria = idCategoria
    }

    override fun addCategoria(view: View?){
        _nuevoPictoDialogCategoria.value = true
    }

    override fun onItemSeleccionadoEntre(idPicto: Int) {
        _idPictoEntretenimiento.value = idPicto
    }

    override fun onNuevoPicto(imagenPicto: String?) {
        _imagenNuevoPicto.value = imagenPicto
    }

    fun getPictogramas(query: String, isNuevoPictoBusqueda: Boolean, activity: Activity) {
        busquedaOpen = true
        val pictogramasBusqueda = ArrayList<Pictograma>()
        CoroutineScope(Dispatchers.IO).launch {
            val dict = CommonUtils.getDataApi(query)

            withContext(Dispatchers.Main) {
                dict.keys.mapNotNull { key ->
                    dict[key]?.let { (value, id) ->
                        pictogramasBusqueda.add(crearPictoBusqueda(key, value, id, activity))
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

    override fun borrarCategoria(posicion: Int, idCategoria: Int, view: View?){
        //dialog
        val dialogo = Dialog(view!!.context)
        dialogo.setContentView(R.layout.dialogo_borrar_categoria)
        dialogo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnBorrar : Button = dialogo.findViewById(R.id.btn_eliminarCategoria)
        val iconoCerrarLogin : ImageView = dialogo.findViewById(R.id.icono_CerrarDialogo)

        btnBorrar.setOnClickListener{
            categoria.eliminarCategoria(view.context as Activity, idUsuario, idCategoria)
            listaCategorias.removeAt(posicion)
            _deletedCategoria.value = posicion
            dialogo.dismiss()
        }

        iconoCerrarLogin.setOnClickListener { dialogo.dismiss() }

        dialogo.show()
    }


    fun createReloj24(hora: Int, minutos: Int, context: Context): MaterialTimePicker {
        return MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hora)
            .setMinute(minutos)
            .setTheme(R.style.TimePicker)
            .setTitleText(getString(context, R.string.selecciona_hora))
            .build()
    }

}