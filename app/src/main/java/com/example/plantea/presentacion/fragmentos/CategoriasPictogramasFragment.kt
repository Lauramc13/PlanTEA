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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramas
import com.example.plantea.presentacion.viewModels.CrearPlanViewModel

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

        context?.let { CommonUtils.getGridValueCuaderno(vista, context, recyclerPictogramas, constraintLayout, 150, 210) }

        imageCerrar = vista.findViewById(R.id.image_CerrarContenedor)
        imageAdd = vista.findViewById(R.id.image_add)
        textoVacio = vista.findViewById(R.id.textoNoPictogramas)
        val adaptador = AdaptadorPictogramas(viewModel._listaPictogramas.value, this, this)
        recyclerPictogramas.adapter = adaptador

        if(viewModel._listaPictogramas.value?.size == 0){
            textoVacio.visibility = View.VISIBLE
        }

        //Este método se ejecutará al pinchar sobre la imagen de cerrar
        imageCerrar.setOnClickListener {
            viewModel._closeFragment.value = true
        }

        if(viewModel.identificadorCategoria == 10){
            imageAdd.visibility = View.GONE
        }

        //Este método se ejecutará al seleccionar añadir nuevo pictograma.
        imageAdd.setOnClickListener { viewModel.nuevoPictogramaDialogo() }

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

    fun markAsFavorite(pictogram: Pictograma) {
        viewModel.pictograma.insertarFavorito(this.requireContext(), viewModel.idUsuario, pictogram.id, pictogram.titulo, pictogram.imagen, pictogram.sourceAPI)
    }

    fun removeFavorite(pictogram: Pictograma) {
        viewModel.pictograma.borrarFavorito(this.requireContext(), viewModel.idUsuario, pictogram.id)
    }
}


