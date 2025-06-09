package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.gestores.GestionCategorias
import com.example.plantea.dominio.objetos.Categoria
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorCategorias
import com.example.plantea.presentacion.viewModels.CrearPlanViewModel
import java.util.Locale

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

        val gCategoria = GestionCategorias()
        viewModel.listaCategorias = gCategoria.obtenerCategoriasPrincipales(actividad, viewModel.idUsuarioTEA, viewModel.idUsuario, Locale.getDefault().language)
        val categoriaNueva = Categoria(viewModel.listaCategorias.size - 1, getString(R.string.lbl_NuevaCategoria).uppercase(), null, "default")
        viewModel.listaCategorias.add(viewModel.listaCategorias.size, categoriaNueva)

        adaptador = AdaptadorCategorias(viewModel.listaCategorias, viewModel)
        val constraintLayout = vista.findViewById<ConstraintLayout>(R.id.frameLayout)
        val recyclerCategorias = vista.findViewById<RecyclerView>(R.id.lst_categorias)
        CommonUtils.getGridValueContainer(vista, context, recyclerCategorias, constraintLayout, 140, 200)

        //recyclerCategorias.layoutManager =  GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        recyclerCategorias.adapter = adaptador

        viewModel.seCreatedCategoria.observe(viewLifecycleOwner) {
            if (it) {
                adaptador.notifyItemInserted(viewModel.listaCategorias.size-2)
            }
        }

        viewModel.seDeletedCategoria.observe(viewLifecycleOwner) {
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