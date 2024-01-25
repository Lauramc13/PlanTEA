package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Categoria
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.fragmentos.CategoriasFragment
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class CrearPlanViewModel : ViewModel() {

    val _identificadorCategoria = MutableLiveData<Int>()
    val _identificadorSubCategoria = MutableLiveData<Int>()
    var _closeFragment = MutableLiveData<Boolean>()
    var _clearBusqueda = MutableLiveData<Boolean>()
    val _pictogramaSeleccionado = MutableLiveData<Pictograma>()
    val _nuevoPictoDialog = MutableLiveData<Boolean>()
    var subcategoriaOpen = false
    var categoriaPicto = 0
    lateinit var tituloPicto: String
    lateinit var imagenPicto: String
    var isEdited = false
    var pictograma = Pictograma()
    var categoria = Categoria()
    val planificacion = Planificacion()

    var fragmentPictogramas = CategoriasPictogramasFragment()
    var fragmentSubcategoria = CategoriasPictogramasFragment()
    var fragmentBusqueda = CategoriasPictogramasFragment()

    //var fragmentBackStackEntry : MutableList<Fragment> = ArrayList()

    @SuppressLint("StaticFieldLeak")
    var activity: Activity = Activity()
    var idUsuario = "0"

    var listaPlanificacion =  ArrayList<Pictograma>()
    var listaPictogramas  = ArrayList<Pictograma>()

    //Opcion para indicar funcionalidad editar o crear
    var opcionEditar = false

    /*fun <T> MutableLiveData<T>.forceRefresh() {
        this.value = this.value
    }*/

    fun setIdUsuario(prefs: android.content.SharedPreferences) {
        val userId = prefs.getString("idUsuario", "")
        idUsuario = userId.toString()
    }

    //Método para mostrar categoria correspondiente
    fun mostrarCategoria(idCategoria: Int, context: Context) {
        listaPictogramas = if (idCategoria == 10) {
            pictograma.obtenerFavoritos(context, idUsuario)
        } else {
            pictograma.obtenerPictogramas(
                context,
                idCategoria,
                idUsuario
            ) as ArrayList<Pictograma>
        }
        _identificadorCategoria.value = idCategoria
    }


    //Método para mostrar los pictogramas correspondientes a las categorias de consultas
    fun mostrarsubCategoria(tituloCategoria: String?, context: Context) {
        //labelBuscando.visibility = View.GONE
        val categoria = categoria.obtenerCategoria(context, tituloCategoria)
        subcategoriaOpen = true
        listaPictogramas = pictograma.obtenerPictogramas(context, categoria, idUsuario) as ArrayList<Pictograma>
        _identificadorSubCategoria.value = categoria
    }

    fun pictogramaSeleccionado(posicion: Int) {
        listaPlanificacion.add(listaPictogramas[posicion])
        isEdited = true
        _pictogramaSeleccionado.value = listaPictogramas[posicion]
    }

    fun nuevoPictogramaDialogo(){
        _nuevoPictoDialog.value = true
    }

    fun crearPictoBusqueda(bitmap: Bitmap, titulo: String?, id: Int, activity: Activity) {
        val tituloMayus = titulo?.uppercase()
        val favorito = pictograma.getFavorito(activity, id.toString(), idUsuario)
        val archivo = CommonUtils.crearImagen(bitmap, titulo, activity)
        listaPictogramas.add(Pictograma(id.toString(), tituloMayus, archivo, 0, 0, favorito, true))
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

    fun cambiarFragmentCategoria(transaction: FragmentTransaction){
        val bundle = Bundle()
        bundle.putSerializable("key", listaPictogramas)
        fragmentPictogramas.arguments = bundle
        transaction.add(R.id.contenedor_fragments, fragmentPictogramas)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun cambiarFragmentBusqueda(transaction: FragmentTransaction){
        subcategoriaOpen = false
        val bundle = Bundle()
        bundle.putSerializable("key", listaPictogramas)
        fragmentBusqueda.arguments = bundle
        transaction.add(R.id.contenedor_fragments, fragmentBusqueda, "FragmentBusqueda")
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun cambiarFragmentSubCategoria(transaction: FragmentTransaction){
        val bundle = Bundle()
        bundle.putSerializable("key", listaPictogramas)
        fragmentSubcategoria.arguments = bundle
        transaction.add(R.id.contenedor_fragments, fragmentSubcategoria)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    //TODO: cuando se gira la pantalla con una subcategoria abierta y se intenta cerrar el fragmento, no se hace nada y se queda en la misma pantalla
    // si se pulsa una segunda vez se va a la pantalla categorias y por ultimo, si se vuelve a abrir una subcategoria, entonces ahi si va el boton de cerrar
    fun closeFragment(transaction: FragmentTransaction, context: Context){
        if(subcategoriaOpen){
            listaPictogramas = _identificadorCategoria.value?.let { idCategoria -> pictograma.obtenerPictogramas(context, idCategoria, idUsuario) } as java.util.ArrayList<Pictograma>
            val bundle = Bundle()
            bundle.putSerializable("key", listaPictogramas)
            fragmentPictogramas.arguments = bundle
            transaction.replace(R.id.contenedor_fragments, fragmentPictogramas)
        }else{
            transaction.replace(R.id.contenedor_fragments, CategoriasFragment())
            _clearBusqueda.value = true
        }
        transaction.addToBackStack(null)
        transaction.commit()
        subcategoriaOpen = false
    }


    fun escalarImagen(context: Context, imagen: String, image: Bitmap): String{
        val proporcion = 500 / image.width.toFloat()
        val imagenFinal = Bitmap.createScaledBitmap(image, 500, (image.height * proporcion).toInt(), false)

        //Crear ruta y guardar imagen
        val ruta = guardarImagen(context, imagen, imagenFinal)

        return ruta

    }

}