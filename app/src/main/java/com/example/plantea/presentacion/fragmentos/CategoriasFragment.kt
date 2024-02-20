package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Categoria
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorCategorias
import com.example.plantea.presentacion.viewModels.CrearPlanViewModel

class CategoriasFragment : Fragment() {
    lateinit var actividad: Activity
    private lateinit var adaptador : AdaptadorCategorias

    private val viewModel: CrearPlanViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val vista = inflater.inflate(R.layout.fragment_categorias, container, false)

        if(savedInstanceState == null) {
            val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
            viewModel.setIdUsuario(prefs)
        }

        val categoria = Categoria()
        viewModel.listaCategorias = categoria.obtenerCategorias(actividad, viewModel.idUsuario)
        categoria.titulo = "Añadir categoria"
        categoria.color = "default"
        viewModel.listaCategorias.add(viewModel.listaCategorias.size, categoria)

        adaptador = AdaptadorCategorias(viewModel.listaCategorias, viewModel)
        val constraintLayout = vista.findViewById<ConstraintLayout>(R.id.frameLayout)
        val recyclerCategorias = vista.findViewById<RecyclerView>(R.id.lst_categorias)
        CommonUtils.getGridValueCuaderno(vista, context, recyclerCategorias, constraintLayout, 140, 180)

        //recyclerCategorias.layoutManager =  GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        recyclerCategorias.adapter = adaptador

        viewModel._createdCategoria.observe(viewLifecycleOwner) {
            if (it) {
                adaptador.notifyItemInserted(viewModel.listaCategorias.size-2)
            }
        }

        viewModel._deletedCategoria.observe(viewLifecycleOwner) {
            if (it != -1) {
                adaptador.notifyItemRemoved(it)
            }
        }

        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
        }
    }

}