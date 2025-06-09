package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.objetos.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramas
import com.example.plantea.presentacion.viewModels.CrearPlanViewModel
import com.google.firebase.auth.TotpMultiFactorAssertion

class CategoriasPictogramasFragment : Fragment(), AdaptadorPictogramas.OnItemSelectedListener {
    lateinit var actividad: Activity

    lateinit var recyclerPictogramas: RecyclerView
    lateinit var textoVacio : TextView
    private lateinit var imageCerrar: ImageView
    private lateinit var imageAdd: Button
    private lateinit var constraintLayout: ConstraintLayout

    private val viewModel: CrearPlanViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val vista = inflater.inflate(R.layout.fragment_categorias_pictogramas, container, false)

        recyclerPictogramas = vista.findViewById(R.id.recycler_Pictogramas)
        constraintLayout = vista.findViewById(R.id.frameLayout)

        context?.let { CommonUtils.getGridValueContainer(vista, context, recyclerPictogramas, constraintLayout, 150, 210) }

        imageCerrar = vista.findViewById(R.id.image_CerrarContenedor)
        imageAdd = vista.findViewById(R.id.image_add)
        textoVacio = vista.findViewById(R.id.textoNoPictogramas)
        viewModel.adaptadorCategoriaPictograma = AdaptadorPictogramas(viewModel.selistaPictogramas.value, this, this)
        recyclerPictogramas.adapter = viewModel.adaptadorCategoriaPictograma

        if(viewModel.selistaPictogramas.value?.size == 0){
            textoVacio.visibility = View.VISIBLE
        }

        //Este método se ejecutará al pinchar sobre la imagen de cerrar
        imageCerrar.setOnClickListener {
            viewModel.seCloseFragment.value = true
        }

        if(viewModel.identificadorCategoria == 10){
            imageAdd.visibility = View.GONE
        }

        //Este método se ejecutará al seleccionar añadir nuevo pictograma
        imageAdd.setOnClickListener { viewModel.nuevoPictogramaDialogo(this.requireActivity()) }

        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
        }
    }

    //Este método se ejecutará al seleccionar un pictograma para añadirlo en la planificacion
    override fun onItemSeleccionado(posicion: Int) {
       /* if (viewModel._listaPictogramas.value?.get(posicion)?.categoria in 1..4) {
            viewModel.mostrarsubCategoria(viewModel._listaPictogramas.value?.get(posicion)?.titulo, this.requireContext())
        } else {*/
            viewModel.pictogramaSeleccionado(posicion)
       // }
    }

    override fun onItemBorrar(pictogram: Pictograma) {
        Toast.makeText(this.requireContext(), "Borrar pictograma", Toast.LENGTH_SHORT).show()
        viewModel.gPicto.borrarPictograma(pictogram.id, viewModel.idUsuario, actividad)
    }

    fun markAsFavorite(pictogram: Pictograma) {
        viewModel.gPicto.insertarFavorito(this.requireContext(), viewModel.idUsuario, pictogram.id, pictogram.titulo, pictogram.idAPI)
    }

    fun removeFavorite(pictogram: Pictograma) {
        viewModel.gPicto.borrarFavorito(this.requireContext(), viewModel.idUsuario, pictogram.id)
    }
}


