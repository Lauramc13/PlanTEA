package com.example.plantea.presentacion.fragmentos.cuaderno

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.CuadernoActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasCuaderno
import com.example.plantea.presentacion.viewModels.CuadernoViewModel
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID


class CuadernoPictoEditFragment : Fragment(){
    lateinit var actividad: Activity
    private lateinit var lst_Pictogramas: RecyclerView
    private lateinit var imageCerrar: ImageView
    private lateinit var imageAtras: ImageView
    private lateinit var adaptador: AdaptadorPictogramasCuaderno
    lateinit var termometro: LinearLayout
    lateinit var searchBar: SearchView
    private lateinit var constraintLayout: ConstraintLayout

    private val viewModel: CuadernoViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val vista = inflater.inflate(R.layout.fragment_cuaderno_pictogramas_edit, container, false)
        val bundle = arguments

        viewModel.listaPictogramas = (bundle?.get("key") as ArrayList<Pictograma>?)!!
        viewModel.isTermometro = (bundle?.get("termometro") as Boolean)
        viewModel.idCuaderno =  (bundle["idCuaderno"] as Int)
        viewModel.tituloCuaderno = (bundle["tituloCuaderno"] as String)

        if(viewModel.listaPictogramas!!.lastIndex != 0 && viewModel.listaPictogramas!![viewModel.listaPictogramas!!.lastIndex].titulo != "AÑADIR PICTOGRAMA") {
            viewModel.listaPictogramas!!.add(Pictograma("AÑADIR PICTOGRAMA", "archivo", 0, 0))
        }

        searchBar = vista.findViewById(R.id.searchViewPicto)
        imageCerrar = vista.findViewById(R.id.icono_cuaderno_fragment)
        imageAtras = vista.findViewById(R.id.icono_cuaderno_fragment_atras)
        lst_Pictogramas = vista.findViewById(R.id.lst_cuaderno_pictogramas)
        val txtCuaderno = vista.findViewById<TextView>(R.id.titulo_cuaderno)
        txtCuaderno.text = viewModel.tituloCuaderno

        constraintLayout = vista.findViewById(R.id.frameLayout)
        CommonUtils.getGridValueCuaderno(vista, context, lst_Pictogramas, constraintLayout)

        val prefs = context?.getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.isPlanificador = prefs?.getBoolean("PlanificadorLogged", false) == true

        val parentActivity = activity as CuadernoActivity?

        adaptador = viewModel.isPlanificador.let { context?.let { it1 -> AdaptadorPictogramasCuaderno(viewModel.listaPictogramas, it, viewModel, it1) }}!!
        adaptador.isBusqueda = viewModel.isBusqueda
        adaptador.listaPictosAgregados = viewModel.listaPictosAgregados

        lst_Pictogramas.adapter = adaptador

        if(savedInstanceState != null){
            if(viewModel.isBusqueda){
                imageCerrar.visibility = View.INVISIBLE
                imageAtras.visibility = View.VISIBLE
                viewModel.listaPictogramas!!.removeAt(viewModel.listaPictogramas!!.lastIndex)
                adaptador.notifyDataSetChanged()
                searchBar.setQuery(viewModel._queryBusqueda.value, false) //TODO: No funciona
            }
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
        viewModel.createPickMedia(this, context)

        //observers()

        return vista
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
                viewModel._queryBusqueda.value = query.trim()
                context?.let { CommonUtils.hideKeyboard(it, searchBar) }
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                newText.trim()
                return true
            }
        })
    }

    fun mostrarPictogramasBusqueda(newPictogramasList: ArrayList<Pictograma>?, listaPicto: ArrayList<String>){
        adaptador.isBusqueda = true
        adaptador.listaPictosAgregados = listaPicto
        viewModel.listaPictogramas = newPictogramasList!!
        adaptador.notifyDataSetChanged()
        imageCerrar.visibility = View.INVISIBLE
        imageAtras.visibility = View.VISIBLE
    }

    fun updateDataRemove(pictograma: Pictograma){
        adaptador.isBusqueda = false
        if(!viewModel.isBusqueda){
            adaptador.notifyItemRemoved(viewModel.listaPictogramas!!.indexOf(pictograma))
        }
        viewModel.listaPictogramas!!.remove(pictograma)
    }


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
        val dialogo = context?.let { Dialog(it) }
        dialogo!!.setContentView(R.layout.dialogo_crear_pictograma_cuaderno)
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
                Toast.makeText(context, "Tienes que rellenar todos los campos", Toast.LENGTH_LONG).show()
            }else{
                crearPicto(nombre)
                dialogo.dismiss()
            }
        }
        iconoCerrarLogin.setOnClickListener { dialogo.dismiss() }
        dialogo.show()
    }

    fun crearPicto(nombre: TextInputLayout){
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

}