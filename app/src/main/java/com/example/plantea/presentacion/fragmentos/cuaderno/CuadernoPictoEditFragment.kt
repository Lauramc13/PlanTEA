package com.example.plantea.presentacion.fragmentos.cuaderno

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.ColorStateList
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
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.CuadernoInterface
import com.example.plantea.presentacion.actividades.CommonUtils
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
    private lateinit var seekbar: SeekBar
    private lateinit var adaptador: AdaptadorPictogramasCuaderno
    lateinit var termometro: LinearLayout
    lateinit var searchbar: SearchView
    private var isTermometro: Boolean = true
    private var isPlanificador : Boolean = true
    private val pictograma = Pictograma()
    lateinit var image: ShapeableImageView
    private var idCuaderno : Int = 0


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val gridValue = context?.let { CommonUtils.cambioOrientacion(it) }
        lst_Pictogramas.layoutManager = gridValue?.let { GridLayoutManager(context, it) }
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
        listaPictogramas.add(Pictograma("AÑADIR CUADERNO", "archivo", 0, 0))

        adaptador.isBusqueda = false
        activity?.runOnUiThread {
            adaptador.notifyDataSetChanged()
        }
        imageCerrar.visibility = View.VISIBLE
        imageAtras.visibility = View.INVISIBLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_cuaderno_pictogramas_edit, container, false)
        val bundle = this.arguments
        listaPictogramas = (bundle!!["key"] as ArrayList<Pictograma>?)!!
        isTermometro = (bundle["termometro"] as Boolean)
        idCuaderno =  (bundle["idCuaderno"] as Int)
        searchbar = vista.findViewById(R.id.searchViewPicto)
        imageCerrar = vista.findViewById(R.id.icono_cuaderno_fragment)
        imageAtras = vista.findViewById(R.id.icono_cuaderno_fragment_atras)
        lst_Pictogramas = vista.findViewById(R.id.lst_cuaderno_pictogramas)

        val gridValue = context?.let { CommonUtils.cambioOrientacion(it) }
        lst_Pictogramas.layoutManager = gridValue?.let { GridLayoutManager(context, it)}
        val prefs = context?.getSharedPreferences("Preferencias", MODE_PRIVATE)

        isPlanificador = prefs?.getBoolean("PlanificadorLogged", false) == true
        if(isPlanificador){
            listaPictogramas.add(Pictograma("AÑADIR CUADERNO", "archivo", 0, 0))
        }
        adaptador = isPlanificador.let { context?.let { it1 ->
            AdaptadorPictogramasCuaderno(listaPictogramas, it, this, it1)
        }}!!

        lst_Pictogramas.adapter = adaptador

        imageCerrar.setOnClickListener { interfaceCuaderno.cerrarFragment() }
        imageAtras.setOnClickListener { interfaceCuaderno.atrasFragment() }
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
        }else {
            mostrarDialogo(posicion)
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

    private fun mostrarDialogo(posicion: Int){
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialogo_termometro)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val pictograma = dialog.findViewById<ShapeableImageView>(R.id.img_pictograma)
        val tituloPictograma = dialog.findViewById<TextView>(R.id.lbl_pictograma)
        val iconoEscuchar = dialog.findViewById<ImageView>(R.id.icono_escuchar)
        seekbar = dialog.findViewById(R.id.seekBar_termometro)
        termometro = dialog.findViewById(R.id.termometro)
        pictograma.setImageURI(Uri.parse(listaPictogramas[posicion].imagen))
        tituloPictograma.text = listaPictogramas[posicion].titulo
        if(!isTermometro){
            termometro.visibility = View.GONE
        }

        iconoEscuchar.setOnClickListener {
            CommonUtils.textToSpeechWord(listaPictogramas[posicion].titulo)
        }

        //Botón cerrar
        imageCerrar = dialog.findViewById(R.id.icono_CerrarDialogoEvento)
        imageCerrar.setOnClickListener { dialog.dismiss() }
        dialog.show()

        //Funcionalidad termómetro: cambio de color según el progreso
        seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress < 45) {
                    seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(138, 255, 126))
                } else if (progress < 90) {
                    seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(255, 193, 79))
                } else if (progress < 100) {
                    seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(239, 35, 60))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // No hacemos nada con esto
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // No hacemos nada con esto
            }
        })
        dialog.show()
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
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

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