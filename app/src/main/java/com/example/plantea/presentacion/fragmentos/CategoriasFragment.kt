package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.plantea.R
import com.example.plantea.presentacion.CrearPlanInterface

class CategoriasFragment : Fragment() {
    lateinit var vista: View
    lateinit var actividad: Activity
    lateinit var cardConsultas: CardView
    lateinit var cardProfesionales: CardView
    lateinit var cardLugares: CardView
    lateinit var cardDesplazamiento: CardView
    lateinit var cardAccion: CardView
    lateinit var cardRecompensa: CardView
    lateinit var cardEntretenimiento: CardView
    lateinit var interfaceCategorias: CrearPlanInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val orientation = resources.configuration.orientation
        val layoutId = if (orientation == Configuration.ORIENTATION_LANDSCAPE) R.layout.fragment_categorias else R.layout.fragment_categorias_portrait
        vista = inflater.inflate(layoutId, container, false)
        cardConsultas = vista.findViewById(R.id.categoria_consultas)
        cardProfesionales = vista.findViewById(R.id.categoria_profesionales)
        cardAccion = vista.findViewById(R.id.categoria_accion)
        cardDesplazamiento = vista.findViewById(R.id.categoria_desplazamiento)
        cardEntretenimiento = vista.findViewById(R.id.categoria_entretenimiento)
        cardLugares = vista.findViewById(R.id.categoria_lugares)
        cardRecompensa = vista.findViewById(R.id.categoria_recompensa)
        cardConsultas.setOnClickListener { interfaceCategorias.mostrarCategoria(1) }
        cardProfesionales.setOnClickListener { interfaceCategorias.mostrarCategoria(2) }
        cardLugares.setOnClickListener { interfaceCategorias.mostrarCategoria(3) }
        cardDesplazamiento.setOnClickListener { interfaceCategorias.mostrarCategoria(4) }
        cardAccion.setOnClickListener { interfaceCategorias.mostrarCategoria(5) }
        cardEntretenimiento.setOnClickListener { interfaceCategorias.mostrarCategoria(6) }
        cardRecompensa.setOnClickListener { interfaceCategorias.mostrarCategoria(7) }
        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
            interfaceCategorias = (actividad as CrearPlanInterface?)!!
        }
    }

}