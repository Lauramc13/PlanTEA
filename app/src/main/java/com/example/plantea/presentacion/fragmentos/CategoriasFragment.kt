package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.plantea.R
import com.example.plantea.presentacion.CrearPlanInterface
import com.example.plantea.presentacion.viewModels.CrearPlanViewModel
import com.example.plantea.presentacion.viewModels.CuadernoViewModel
import com.google.android.material.transition.MaterialContainerTransform

class CategoriasFragment : Fragment() {
    lateinit var actividad: Activity

    private val viewModel: CrearPlanViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val vista = inflater.inflate(R.layout.fragment_categorias, container, false)


        val cardMedico = vista.findViewById<CardView>(R.id.categoria_medico)
        val cardCompra = vista.findViewById<CardView>(R.id.categoria_compra)
        val cardPeluqueria = vista.findViewById<CardView>(R.id.categoria_peluqueria)
        val cardColegio = vista.findViewById<CardView>(R.id.categoria_colegio)
        val cardFavoritos = vista.findViewById<CardView>(R.id.categoria_favoritos)
        val cardAccion = vista.findViewById<CardView>(R.id.categoria_accion)
        val cardDesplazamiento = vista.findViewById<CardView>(R.id.categoria_desplazamiento)
        val cardEntretenimiento = vista.findViewById<CardView>(R.id.categoria_entretenimiento)
        val cardLugares = vista.findViewById<CardView>(R.id.categoria_lugares)
        val cardRecompensa = vista.findViewById<CardView>(R.id.categoria_recompensa)

        if(savedInstanceState == null){
            val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
            viewModel.setIdUsuario(prefs)

            cardMedico.setOnClickListener { viewModel.mostrarCategoria(1, this.requireContext()) }
            cardPeluqueria.setOnClickListener { viewModel.mostrarCategoria(2, this.requireContext()) }
            cardCompra.setOnClickListener { viewModel.mostrarCategoria(3, this.requireContext())}
            cardColegio.setOnClickListener { viewModel.mostrarCategoria(4, this.requireContext())}
            cardLugares.setOnClickListener { viewModel.mostrarCategoria(5, this.requireContext())}
            cardDesplazamiento.setOnClickListener { viewModel.mostrarCategoria(6, this.requireContext())}
            cardAccion.setOnClickListener { viewModel.mostrarCategoria(7, this.requireContext())}
            cardEntretenimiento.setOnClickListener { viewModel.mostrarCategoria(8, this.requireContext())}
            cardRecompensa.setOnClickListener { viewModel.mostrarCategoria(9, this.requireContext())}
            cardFavoritos.setOnClickListener { viewModel.mostrarCategoria(10, this.requireContext())}
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