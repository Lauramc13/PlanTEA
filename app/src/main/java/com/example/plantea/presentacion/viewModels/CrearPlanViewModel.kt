package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Categoria
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.CrearPlanActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion
import com.example.plantea.presentacion.fragmentos.CategoriasFragment
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class CrearPlanViewModel : ViewModel(), AdaptadorPlanificacion.OnItemSelectedListener{

    var identificadorCategoria : Int = -1
    var identificadorSubCategoria : Int = -1
    var _closeFragment = SingleLiveEvent<Boolean>()
    var _clearBusqueda = SingleLiveEvent<Boolean>()
    val _pictogramaSeleccionado = SingleLiveEvent<Pictograma>()
    val _nuevoPictoDialog = SingleLiveEvent<Boolean>()
    var _historiaClicked = SingleLiveEvent<Int>()

    var subcategoriaOpen = false
    var busquedaOpen = false
    var categoriaPicto = 0
    lateinit var tituloPicto: String
    lateinit var imagenPicto: String
    var isEdited = false
    var pictograma = Pictograma()
    var categoria = Categoria()
    val planificacion = Planificacion()

    var fragmentCategoriasPictogramas = CategoriasPictogramasFragment()

    @SuppressLint("StaticFieldLeak")
    var activity: Activity = Activity()
    var idUsuario = "0"

    var listaPlanificacion =  ArrayList<Pictograma>()
    var _listaPictogramas  = SingleLiveEvent<ArrayList<Pictograma>>()

    //Opcion para indicar funcionalidad editar o crear
    var opcionEditar = false

    var historiaSaved = false

    fun setIdUsuario(prefs: android.content.SharedPreferences) {
        idUsuario = prefs.getString("idUsuario", "").toString()
    }

    //Método para mostrar categoria correspondiente
    fun mostrarCategoria(idCategoria: Int, context: Context) {
        _listaPictogramas.value = if (idCategoria == 10) {
            pictograma.obtenerFavoritos(context, idUsuario)
        } else {
            pictograma.obtenerPictogramas(context, idCategoria, idUsuario) as ArrayList<Pictograma>
        }
        identificadorCategoria = idCategoria
    }


    //Método para mostrar los pictogramas correspondientes a las categorias de consultas
    fun mostrarsubCategoria(tituloCategoria: String?, context: Context) {
        //labelBuscando.visibility = View.GONE
        val categoria = categoria.obtenerCategoria(context, tituloCategoria)
        subcategoriaOpen = true
        _listaPictogramas.value = pictograma.obtenerPictogramas(context, categoria, idUsuario) as ArrayList<Pictograma>
        identificadorSubCategoria = categoria

    }

    fun pictogramaSeleccionado(posicion: Int) {
        _listaPictogramas.value?.let { listaPlanificacion.add(it[posicion]) }
        isEdited = true
        _pictogramaSeleccionado.value = _listaPictogramas.value?.get(posicion)
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
                _listaPictogramas.value = pictograma.obtenerPictogramas(context, identificadorCategoria, idUsuario)  as ArrayList<Pictograma>
                subcategoriaOpen = false
            }else{
                transaction.replace(R.id.contenedor_fragments, CategoriasFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        _clearBusqueda.value = true
    }

    override fun onHistoriaClick(position: Int){
        _historiaClicked.value = position
    }
}