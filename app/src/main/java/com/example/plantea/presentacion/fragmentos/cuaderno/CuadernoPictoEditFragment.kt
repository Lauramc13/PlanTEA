package com.example.plantea.presentacion.fragmentos.cuaderno

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.CuadernoInterface
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.ninio.CuadernoActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasCuaderno
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID


class CuadernoPictoEditFragment : Fragment(), AdaptadorPictogramasCuaderno.OnItemSelectedListener {
    lateinit var vista: View
    lateinit var actividad: Activity
    lateinit var listaPictogramas: ArrayList<Pictograma>
    private lateinit var interfaceCuaderno: CuadernoInterface
    private lateinit var lst_Pictogramas: RecyclerView
    private lateinit var imageCerrar: ImageView
    private lateinit var imageAtras: ImageView
    private lateinit var adaptador: AdaptadorPictogramasCuaderno
    lateinit var termometro: LinearLayout
    lateinit var searchbar: SearchView
    private var isTermometro: Boolean = true
    private var isPlanificador : Boolean = true
    private var tituloCuaderno : String = ""
    private val pictograma = Pictograma()
    lateinit var image: ShapeableImageView
    private var idCuaderno : Int = 0
    private lateinit var constraintLayout: ConstraintLayout
    private var changedUiMode = false

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        CommonUtils.getGridValueCuaderno(vista, context, lst_Pictogramas, constraintLayout)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("CHANGED_KEY", true) // this is so we know that the UI mode has changed
    }

    fun mostrarPictogramasBusqueda(newPictogramasList: ArrayList<Pictograma>?, listaPicto: ArrayList<String>){
        adaptador.isBusqueda = true
        adaptador.listaPictosAgregados = listaPicto
        listaPictogramas = newPictogramasList!!
        adaptador.notifyDataSetChanged()
        imageCerrar.visibility = View.INVISIBLE
        imageAtras.visibility = View.VISIBLE
    }

    fun updateDataRemove(pictograma: Pictograma){
        adaptador.isBusqueda = false
        listaPictogramas.remove(pictograma)
        adaptador.notifyDataSetChanged()
        imageCerrar.visibility = View.VISIBLE
        imageAtras.visibility = View.INVISIBLE
    }

    fun updateData(newPictogramasList: ArrayList<Pictograma>){
        listaPictogramas.clear()
        listaPictogramas.addAll(newPictogramasList)
        //if savedInstance == null then add picto
        if(listaPictogramas.lastIndex != 0)
            listaPictogramas.add(Pictograma("AAÑADIR PICTOGRAMA", "archivo", 0, 0))

        adaptador.isBusqueda = false
        activity?.runOnUiThread {
            adaptador.notifyDataSetChanged()
        }
        imageCerrar.visibility = View.VISIBLE
        imageAtras.visibility = View.INVISIBLE
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_cuaderno_pictogramas_edit, container, false)
        val bundle = arguments

        listaPictogramas = (bundle?.get("key") as ArrayList<Pictograma>?)!!
        isTermometro = (bundle?.get("termometro") as Boolean)
        idCuaderno =  (bundle["idCuaderno"] as Int)
        tituloCuaderno = (bundle["tituloCuaderno"] as String)

        searchbar = vista.findViewById(R.id.searchViewPicto)
        imageCerrar = vista.findViewById(R.id.icono_cuaderno_fragment)
        imageAtras = vista.findViewById(R.id.icono_cuaderno_fragment_atras)
        lst_Pictogramas = vista.findViewById(R.id.lst_cuaderno_pictogramas)
        val txtCuaderno = vista.findViewById<TextView>(R.id.titulo_cuaderno)
        txtCuaderno.text = tituloCuaderno

        constraintLayout = vista.findViewById(R.id.frameLayout)
        CommonUtils.getGridValueCuaderno(vista, context, lst_Pictogramas, constraintLayout)

        val prefs = context?.getSharedPreferences("Preferencias", MODE_PRIVATE)

        isPlanificador = prefs?.getBoolean("PlanificadorLogged", false) == true
        if (savedInstanceState != null) {
            changedUiMode = savedInstanceState.getBoolean("CHANGED_KEY")
        }
        if(!changedUiMode){
            listaPictogramas.add(Pictograma("AÑADIR PICTOGRAMA", "archivo", 0, 0))
        }

        val cuadernoActivity = activity as CuadernoActivity

        adaptador = isPlanificador.let { context?.let { it1 -> AdaptadorPictogramasCuaderno(listaPictogramas, it, this, it1) }}!!
        adaptador.isBusqueda = cuadernoActivity.isBusqueda
        adaptador.listaPictosAgregados = cuadernoActivity.listaPictosAgregados

        lst_Pictogramas.adapter = adaptador

        if(savedInstanceState != null){
            if(cuadernoActivity.isBusqueda){
                    imageCerrar.visibility = View.INVISIBLE
                    imageAtras.visibility = View.VISIBLE
                    listaPictogramas.removeAt(listaPictogramas.lastIndex)
                    adaptador.notifyDataSetChanged()
                }
        }

        imageCerrar.setOnClickListener { interfaceCuaderno.cerrarFragment() }
        imageAtras.setOnClickListener { interfaceCuaderno.atrasFragment(isTermometro, tituloCuaderno) }
        busqueda()

        context?.let { CommonUtils.initializeTextToSpeech(it) }

        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
            interfaceCuaderno = (actividad as CuadernoInterface?)!!
        }
    }

    override fun pictogramaCuaderno(posicion: Int) {
        if(posicion == listaPictogramas.lastIndex && isPlanificador){
            mostrarDialogoCrearPicto()
        }
    }

    override fun addPicto(pictograma: Pictograma) {
        interfaceCuaderno.addPictoFromBusqueda(pictograma)
    }

    override fun removePicto(pictograma: Pictograma, sourceAPI: Boolean, isBusqueda: Boolean) {
        interfaceCuaderno.removePicto(pictograma, sourceAPI, isBusqueda)
    }

    private fun busqueda(){
        searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                interfaceCuaderno.mostrarPictogramasBusqueda(query.trim())
                context?.let { CommonUtils.hideKeyboard(it, searchbar) }
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                newText.trim()
                return true
            }
        })
    }

    private fun mostrarDialogoCrearPicto(){
        val dialogo = context?.let { Dialog(it) }
        dialogo!!.setContentView(R.layout.dialogo_crear_pictograma_cuaderno)
        dialogo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val nombre : TextInputLayout = dialogo.findViewById(R.id.txt_title)
        image = dialogo.findViewById(R.id.img)

        val btnCrear : Button = dialogo.findViewById(R.id.btn_create)
        val iconoCerrarLogin : ImageView = dialogo.findViewById(R.id.icono_CerrarDialogo)

        image.setOnClickListener {
            abrirGaleria()
        }

        //el termometro por ahora no hace nada
        btnCrear.setOnClickListener{
            nombre.error = null
            if (nombre.editText?.text.toString().isEmpty() || image.drawable == null) {
                nombre.error = "Obligatorio"
                Toast.makeText(context, "Tienes que rellenar todos los campos", Toast.LENGTH_LONG).show()
            }else{
                val prefs = context?.getSharedPreferences("Preferencias", MODE_PRIVATE)
                val idUsuario = prefs?.getString("idUsuario", "")
                val numero = UUID.randomUUID()
                val imagen = context?.let { it1 -> CommonUtils.crearRuta(it1, image, "ImgPictograma$numero") }

                if (idUsuario != null) {
                    val id = pictograma.nuevoPictogramaCuaderno(activity, nombre.editText?.text.toString(), imagen, idCuaderno, idUsuario)
                    val newPictograma = Pictograma()
                    newPictograma.id = id.toString()
                    newPictograma.titulo = nombre.editText?.text.toString()
                    newPictograma.imagen = imagen
                    val lastIndex = listaPictogramas.size - 1
                    listaPictogramas.add(lastIndex, newPictograma)
                    interfaceCuaderno.addPictoPersonalizado(newPictograma)
                }
                adaptador.notifyDataSetChanged()
                dialogo.dismiss()
            }
        }
        iconoCerrarLogin.setOnClickListener { dialogo.dismiss() }
        dialogo.show()
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        if(pickMedia != null){
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }else{
            Log.d("TAG", "Error al abrir la galería")
            Toast.makeText(context, "Error al abrir la galería", Toast.LENGTH_SHORT).show()
        }

    }


    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        // Manejar la URI devuelta aquí
        if (uri != null) {
            // Load the selected image from the URI
            val inputStream = context?.contentResolver?.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Guardar la imagen en rutaUsuarioTEA
            val ruta = context?.let { CommonUtils.getPathFromUri(it, uri)}

            context?.let {
                if (ruta != null) {
                    CommonUtils.guardarImagen(it, ruta, bitmap)
                    image.background = null
                    image.setImageURI(uri)
                }
            }

        } else {
            Toast.makeText(context, "No se ha seleccionado una imagen", Toast.LENGTH_SHORT).show()
        }
    }


}