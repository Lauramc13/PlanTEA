package com.example.plantea.presentacion.fragmentos.cuaderno

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasCuaderno
import com.example.plantea.presentacion.viewModels.CuadernoViewModel
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID

class CuadernoPictoEditFragment : Fragment(), AdaptadorPictogramasCuaderno.OnItemSelectedListener{
    lateinit var actividad: Activity
    private lateinit var lstPictogramas: RecyclerView
    private lateinit var imageCerrar: ImageView
    private lateinit var imageAtras: ImageView
    private lateinit var adaptador: AdaptadorPictogramasCuaderno
    lateinit var termometro: LinearLayout
    lateinit var searchBar: SearchView
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var seekbar: SeekBar

    private val viewModel: CuadernoViewModel by activityViewModels()

    private lateinit var vista: View

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_cuaderno_pictogramas_edit, container, false)
        val bundle = arguments

        viewModel.listaPictogramas = (bundle?.get("key") as ArrayList<Pictograma>?)!!
        viewModel.isTermometro = (bundle?.get("termometro") as Boolean)

        if(viewModel.listaPictogramas?.isEmpty() == true){
            viewModel.listaPictogramas!!.add(Pictograma("AÑADIR PICTOGRAMA", "archivo", 0, 0))
        }else{
            if(viewModel.listaPictogramas?.last()?.titulo != "AÑADIR PICTOGRAMA" ) {
                viewModel.listaPictogramas!!.add(Pictograma("AÑADIR PICTOGRAMA", "archivo", 0, 0))
            }
        }

        searchBar = vista.findViewById(R.id.searchViewPicto)
        imageCerrar = vista.findViewById(R.id.icono_cuaderno_fragment)
        imageAtras = vista.findViewById(R.id.icono_cuaderno_fragment_atras)
        lstPictogramas = vista.findViewById(R.id.lst_cuaderno_pictogramas)
        val txtCuaderno = vista.findViewById<TextView>(R.id.titulo_cuaderno)
        txtCuaderno.text = viewModel.tituloCuaderno

        constraintLayout = vista.findViewById(R.id.frameLayout)
        CommonUtils.getGridValueCuaderno(vista, context, lstPictogramas, constraintLayout, 150, 200)

        val prefs = context?.getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.isPlanificador = prefs?.getBoolean("PlanificadorLogged", false) == true

        adaptador = viewModel.isPlanificador.let { context?.let { it1 -> AdaptadorPictogramasCuaderno(viewModel.listaPictogramas, it, this, it1) }}!!
        adaptador.isBusqueda = viewModel.isBusqueda
        adaptador.listaPictosAgregados = viewModel.listaPictosAgregados

        lstPictogramas.adapter = adaptador

        if(viewModel.isBusqueda){
            imageCerrar.visibility = View.INVISIBLE
            imageAtras.visibility = View.VISIBLE
            viewModel.listaPictogramas!!.removeAt(viewModel.listaPictogramas!!.lastIndex)
            adaptador.notifyDataSetChanged()
        }


        imageCerrar.setOnClickListener {
            viewModel._cerrarFragment.value = true
        }

        imageAtras.setOnClickListener {
            searchBar.setQuery("", false)
            searchBar.clearFocus()
            updateData(viewModel.originalPictogramas)
            viewModel.isBusqueda = false
        }

        busqueda()

        context?.let { CommonUtils.initializeTextToSpeech(it) }

        viewModel._image.observe(viewLifecycleOwner){
            viewModel.image.setImageURI(it)
            viewModel.image.background = null
        }
        viewModel.createPickMedia(this, context, vista)

        return vista
    }

    private fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
        }
    }

    private fun busqueda(){
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!CommonUtils.isNetworkAvailable(requireContext())) {
                    CommonUtils.showSnackbar(vista, requireContext(), "No hay conexión a internet")
                    searchBar.setQuery("", false)
                    searchBar.clearFocus()
                }else {
                    viewModel._queryBusqueda.value = query.trim()
                }
                context?.let { CommonUtils.hideKeyboard(it, searchBar) }
                return true

            }
            override fun onQueryTextChange(newText: String): Boolean {
                newText.trim()
                return true
            }
        })
    }

    fun updateDataRemove(pictograma: Pictograma){
        adaptador.isBusqueda = false
        if(!viewModel.isBusqueda){
            adaptador.notifyItemRemoved(viewModel.listaPictogramas!!.indexOf(pictograma))
        }
        viewModel.listaPictogramas!!.remove(pictograma)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun updateData(newPictogramasList: ArrayList<Pictograma>?){
        viewModel.listaPictogramas!!.clear()
        if (newPictogramasList != null) {
            viewModel.listaPictogramas!!.addAll(newPictogramasList)
        }

        if(viewModel.listaPictogramas!!.lastIndex != 0) {
            viewModel.listaPictogramas!!.add(Pictograma("AÑADIR PICTOGRAMA", "archivo", 0, 0))
        }

        adaptador.isBusqueda = false
        activity?.runOnUiThread {
            adaptador.notifyDataSetChanged()
        }

        imageCerrar.visibility = View.VISIBLE
        imageAtras.visibility = View.INVISIBLE
    }

    fun mostrarDialogoCrearPicto(){
        checkIfFragmentAttached {
            val dialogo = Dialog(requireContext())
            dialogo.setContentView(R.layout.dialogo_crear_pictograma_cuaderno)
            dialogo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val nombre : TextInputLayout = dialogo.findViewById(R.id.txt_title)
            viewModel.image = dialogo.findViewById(R.id.img)

            val btnCrear : Button = dialogo.findViewById(R.id.btn_create)
            val iconoCerrarLogin : ImageView = dialogo.findViewById(R.id.icono_CerrarDialogo)

            viewModel.image.setOnClickListener {
                viewModel.abrirGaleria()
            }

            btnCrear.setOnClickListener{
                nombre.error = null
                if (nombre.editText?.text.toString().isEmpty() || viewModel.image.drawable == null) {
                    nombre.error = "Obligatorio"
                    CommonUtils.showSnackbar(dialogo.findViewById(android.R.id.content), requireContext(), "Tienes que rellenar todos los campos")
                }else{
                    crearPicto(nombre)
                    dialogo.dismiss()
                }
            }
            iconoCerrarLogin.setOnClickListener { dialogo.dismiss() }
            dialogo.show()
        }
    }

    private fun crearPicto(nombre: TextInputLayout){
        val prefs = context?.getSharedPreferences("Preferencias", MODE_PRIVATE)
        val idUsuario = prefs?.getString("idUsuario", "")
        val numero = UUID.randomUUID()
        val imagen = context?.let { it1 -> CommonUtils.crearRuta(it1, viewModel.image, "ImgPictograma$numero") }

        if (idUsuario != null) {
            val id = viewModel.picto.nuevoPictogramaCuaderno(activity, nombre.editText?.text.toString(), imagen, viewModel.idCuaderno, idUsuario)
            val newPictograma = Pictograma()
            newPictograma.id = id.toString()
            newPictograma.titulo = nombre.editText?.text.toString()
            newPictograma.imagen = imagen
            val lastIndex = viewModel.listaPictogramas!!.size - 1
            viewModel.listaPictogramas!!.add(lastIndex, newPictograma)
            viewModel.originalPictogramas?.add(newPictograma)
        }

        adaptador.notifyItemChanged(viewModel.listaPictogramas!!.lastIndex-1)
    }

    override fun pictogramaCuaderno(posicion: Int) {
        if(posicion == viewModel.listaPictogramas?.lastIndex && viewModel.isPlanificador){
            mostrarDialogoCrearPicto()
        }else{
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialogo_termometro)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val pictograma = dialog.findViewById<ShapeableImageView>(R.id.img_pictograma)
            val tituloPictograma = dialog.findViewById<TextView>(R.id.lbl_pictograma)
            val iconoEscuchar = dialog.findViewById<ImageView>(R.id.icono_escuchar)
            seekbar = dialog.findViewById(R.id.seekBar_termometro)
            termometro = dialog.findViewById(R.id.termometro)
            pictograma.setImageURI(Uri.parse(viewModel.listaPictogramas?.get(posicion)?.imagen ))
            tituloPictograma.text = viewModel.listaPictogramas?.get(posicion)?.titulo
            if(!viewModel.isTermometro){
                termometro.visibility = View.GONE
            }

            iconoEscuchar.setOnClickListener {
                CommonUtils.textToSpeechWord(viewModel.listaPictogramas?.get(posicion)?.titulo )
            }

            //Botón cerrar
            imageCerrar = dialog.findViewById(R.id.icono_CerrarDialogoEvento)
            imageCerrar.setOnClickListener { dialog.dismiss() }

            //Funcionalidad termómetro: cambio de color según el progreso
            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    // Write code to perform some action when progress is changed.
                    if (progress < 45) {
                        seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(138, 255, 126))
                    } else if (progress < 90) {
                        seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(255, 193, 79))
                    } else if (progress < 100) {
                        seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(239, 35, 60))
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // Write code to perform some action when touch is started.
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    // Write code to perform some action when touch is stopped.
                }
            })
            dialog.show()
        }
    }

    override fun addPicto(pictograma: Pictograma) {
        viewModel._pictoBusquedaAdded.value = pictograma
    }

    override fun removePicto(pictograma: Pictograma, sourceAPI: Boolean, isBusqueda: Boolean) {
        viewModel.isBusqueda = isBusqueda
        viewModel.sourceAPI = sourceAPI
        viewModel._removePicto.value = pictograma
    }
}