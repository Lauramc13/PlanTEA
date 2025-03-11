package com.example.plantea.presentacion.fragmentos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasTraductor
import com.example.plantea.presentacion.viewModels.CrearPlanViewModel
import com.example.plantea.presentacion.viewModels.TraductorViewModel
import com.google.android.material.button.MaterialButton
import java.util.Locale

class TraduccionPlanFragment: Fragment() {
    lateinit var vista: View
    private val viewModel: TraductorViewModel by activityViewModels()
    private val viewModelPlan: CrearPlanViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        CommonUtils.loadLemmatizer(Locale.getDefault().language.lowercase(), requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_traduccion_planificacion, container, false)

        val recyclerView = vista.findViewById<RecyclerView>(R.id.recycler_pictogramas_traduccion)
        val layoutManagerLinear = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManagerLinear

        viewModel.mdListaPictogramasTraduccion.observe(viewLifecycleOwner) { listaPictogramas ->
            viewModel.adaptador = AdaptadorPictogramasTraductor(listaPictogramas, viewModel)
            recyclerView.adapter = viewModel.adaptador
            viewModel.adaptador.notifyDataSetChanged()
        }

        val copyButton = vista.findViewById<MaterialButton>(R.id.copy_pictos)
        copyButton.setOnClickListener {
            val startPosition = viewModelPlan.listaPlanificacion.size
            viewModelPlan.listaPlanificacion.addAll(viewModel.listaPictogramas)
            for ((index, pictograma) in viewModel.listaPictogramas.withIndex()) {
                viewModelPlan.adaptadorPlanificacion.notifyItemInserted(startPosition + index)
                Log.i("pruebas", "Pictograma añadido a la planificación: ${pictograma.idAPI}")
            }
        }

        val cerrar = vista.findViewById<ImageView>(R.id.image_CerrarContenedor)
        cerrar.setOnClickListener {
            viewModelPlan.seCloseFragment.value = true

        }

        return vista
    }
}