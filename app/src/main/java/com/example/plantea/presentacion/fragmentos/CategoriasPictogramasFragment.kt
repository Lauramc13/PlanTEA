package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramas
import com.example.plantea.presentacion.viewModels.CrearPlanViewModel

class CategoriasPictogramasFragment : Fragment(), AdaptadorPictogramas.OnItemSelectedListener {
    lateinit var actividad: Activity

    lateinit var recyclerPictogramas: RecyclerView
    private lateinit var image_Cerrar: ImageView
    private lateinit var image_add: ImageView

    private val viewModel: CrearPlanViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val vista = inflater.inflate(R.layout.fragment_categorias_pictogramas, container, false)

        recyclerPictogramas = vista.findViewById(R.id.recycler_Pictogramas)

        val orientation = resources.configuration.orientation
        val screenWidthInDp = resources.displayMetrics.widthPixels / resources.displayMetrics.density

        val gridValueManager: Int = if (screenWidthInDp< 600) {
            2
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            3
        } else{
            5
        }

        recyclerPictogramas.layoutManager = GridLayoutManager(context, gridValueManager, GridLayoutManager.VERTICAL, false)
        image_Cerrar = vista.findViewById(R.id.image_CerrarContenedor)
        image_add = vista.findViewById(R.id.image_add)
        val adaptador = AdaptadorPictogramas(viewModel.listaPictogramas, this, this)
        recyclerPictogramas.adapter = adaptador

        //Este método se ejecutará al pinchar sobre la imagen de cerrar
        image_Cerrar.setOnClickListener {
            viewModel._closeFragment.value = true
            Log.d("TAG", "image_Cerrar.setOnClickListener")
        }

        //Este método se ejecutará al seleccionar añadir nuevo pictograma.
        image_add.setOnClickListener { viewModel.nuevoPictogramaDialogo() }
        return vista
    }

    fun updateDataFragment(){
        val adaptador = AdaptadorPictogramas(viewModel.listaPictogramas, this, this)
        recyclerPictogramas.adapter = adaptador
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
        }
    }

    //Este método se ejecutará al seleccionar un pictograma para añadirlo en la planificacion
    override fun onItemSeleccionado(posicion: Int) {
        if (viewModel.listaPictogramas[posicion].categoria in 1..4) {
            viewModel.mostrarsubCategoria(viewModel.listaPictogramas[posicion].titulo, this.requireContext())
        } else {
            viewModel.pictogramaSeleccionado(posicion)
        }
    }

    fun markAsFavorite(pictogram: Pictograma) {
        viewModel.pictograma.insertarFavorito(this.requireContext(), viewModel.idUsuario, pictogram.id, pictogram.titulo, pictogram.imagen, pictogram.sourceAPI)
    }

    fun removeFavorite(pictogram: Pictograma) {
        viewModel.pictograma.borrarFavorito(this.requireContext(), viewModel.idUsuario, pictogram.id)
    }


}


