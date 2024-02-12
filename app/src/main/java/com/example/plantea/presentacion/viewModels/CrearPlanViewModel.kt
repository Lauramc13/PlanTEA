package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion
import com.example.plantea.presentacion.fragmentos.CategoriasFragment
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class CrearPlanViewModel : ViewModel(), AdaptadorPlanificacion.OnItemSelectedListener{

    val _identificadorCategoria = MutableLiveData<Int>()
    val _identificadorSubCategoria = MutableLiveData<Int>()
    var _closeFragment = MutableLiveData<Boolean>()
    var _clearBusqueda = MutableLiveData<Boolean>()
    val _pictogramaSeleccionado = MutableLiveData<Pictograma>()
    val _nuevoPictoDialog = MutableLiveData<Boolean>()
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
    var fragment = Fragment()

    @SuppressLint("StaticFieldLeak")
    var activity: Activity = Activity()
    var idUsuario = "0"

    var listaPlanificacion =  ArrayList<Pictograma>()
    var listaPictogramas  = ArrayList<Pictograma>()

    //Opcion para indicar funcionalidad editar o crear
    var opcionEditar = false

    fun setIdUsuario(prefs: android.content.SharedPreferences) {
        idUsuario = prefs.getString("idUsuario", "").toString()
    }

    //Método para mostrar categoria correspondiente
    fun mostrarCategoria(idCategoria: Int, context: Context) {
        listaPictogramas = if (idCategoria == 10) {
            pictograma.obtenerFavoritos(context, idUsuario)
        } else {
            pictograma.obtenerPictogramas(context, idCategoria, idUsuario) as ArrayList<Pictograma>
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

    fun cambiarFragment(transaction: FragmentTransaction, subCategoria: Boolean){
        if(subCategoria){
            fragmentCategoriasPictogramas.updateDataFragment()
        }else{
            transaction.replace(R.id.contenedor_fragments, fragmentCategoriasPictogramas)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    fun cambiarFragmentBusqueda(transaction: FragmentTransaction, fragment: Fragment){ //ESTO HAY QUE QUITARLO Y UNIRLO CON CAMBIARFRAGMENT
        busquedaOpen = true
        if(fragment is CategoriasFragment){
            transaction.replace(R.id.contenedor_fragments, fragmentCategoriasPictogramas)
            transaction.addToBackStack(null)
            transaction.commit()
        }else{
            fragmentCategoriasPictogramas.updateDataFragment()
        }
    }

    fun closeFragment(transaction: FragmentTransaction, context: Context){ //ESTO HAY QUE MEJORARLO
        if(busquedaOpen){
            transaction.replace(R.id.contenedor_fragments, CategoriasFragment())
            transaction.addToBackStack(null)
            transaction.commit()
            busquedaOpen = false
        }else{
            if(subcategoriaOpen){
                listaPictogramas = _identificadorCategoria.value?.let { idCategoria -> pictograma.obtenerPictogramas(context, idCategoria, idUsuario) } as ArrayList<Pictograma>
                fragmentCategoriasPictogramas.updateDataFragment()
                subcategoriaOpen = false
            }else{
                fragmentCategoriasPictogramas.updateDataFragment()
                transaction.replace(R.id.contenedor_fragments, CategoriasFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        _clearBusqueda.value = true
    }

    override fun onHistoriaClick(position: Int): Boolean{
        var historiaSaved = false
        val tituloCard = listaPlanificacion[position].titulo
        val context = fragmentCategoriasPictogramas.requireContext()
        val dialogLogout = Dialog(context)
        dialogLogout.setContentView(R.layout.dialogo_historiasocial)
        dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnGuardar = dialogLogout.findViewById<Button>(R.id.btn_eliminarEvento)
        val cardtitulo = dialogLogout.findViewById<TextView>(R.id.cardName)
        cardtitulo.text = tituloCard
        val iconoCerrarLogin = dialogLogout.findViewById<ImageView>(R.id.icono_CerrarDialogo)
        val historiaText = dialogLogout.findViewById<TextInputLayout>(R.id.historiaText)

        if(listaPlanificacion[position].historia.toString() == "null"){
            historiaText.editText?.setText("")
        }else {
            historiaText.editText?.setText(listaPlanificacion[position].historia)
        }

        iconoCerrarLogin.setOnClickListener { dialogLogout.dismiss() }
        btnGuardar.setOnClickListener{
            if(historiaText.editText?.text.toString() == ""){
                Toast.makeText(context, "No se puede crear una historia vacía", Toast.LENGTH_LONG).show()
            }else {
                listaPlanificacion[position].historia = historiaText.editText?.text.toString()
                historiaSaved = true
                dialogLogout.dismiss()
            }
        }

        dialogLogout.show()

        return historiaSaved
    }
}