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
    //lateinit var cardConsultas: CardView
    lateinit var cardMedico: CardView
    lateinit var cardCompra: CardView
    lateinit var cardPeluqueria: CardView
    lateinit var cardColegio: CardView
    lateinit var cardLugares: CardView
    lateinit var cardDesplazamiento: CardView
    lateinit var cardAccion: CardView
    lateinit var cardRecompensa: CardView
    lateinit var cardFavoritos: CardView
    lateinit var cardEntretenimiento: CardView
    lateinit var interfaceCategorias: CrearPlanInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val orientation = resources.configuration.orientation
        val layoutId = if (orientation == Configuration.ORIENTATION_LANDSCAPE) R.layout.fragment_categorias else R.layout.fragment_categorias_portrait
        vista = inflater.inflate(layoutId, container, false)
        cardMedico = vista.findViewById(R.id.categoria_medico)
        cardCompra = vista.findViewById(R.id.categoria_compra)
        cardPeluqueria = vista.findViewById(R.id.categoria_peluqueria)
        cardColegio = vista.findViewById(R.id.categoria_colegio)
        cardFavoritos = vista.findViewById(R.id.categoria_favoritos)
        cardAccion = vista.findViewById(R.id.categoria_accion)
        cardDesplazamiento = vista.findViewById(R.id.categoria_desplazamiento)
        cardEntretenimiento = vista.findViewById(R.id.categoria_entretenimiento)
        cardLugares = vista.findViewById(R.id.categoria_lugares)
        cardRecompensa = vista.findViewById(R.id.categoria_recompensa)
        cardMedico.setOnClickListener { interfaceCategorias.mostrarCategoria(1) }
        cardPeluqueria.setOnClickListener { interfaceCategorias.mostrarCategoria(2) }
        cardCompra.setOnClickListener { interfaceCategorias.mostrarCategoria(3) }
        cardColegio.setOnClickListener { interfaceCategorias.mostrarCategoria(4) }
        cardLugares.setOnClickListener { interfaceCategorias.mostrarCategoria(5) }
        cardDesplazamiento.setOnClickListener { interfaceCategorias.mostrarCategoria(6) }
        cardAccion.setOnClickListener { interfaceCategorias.mostrarCategoria(7) }
        cardEntretenimiento.setOnClickListener { interfaceCategorias.mostrarCategoria(8) }
        cardRecompensa.setOnClickListener { interfaceCategorias.mostrarCategoria(9) }
        cardFavoritos.setOnClickListener { interfaceCategorias.mostrarCategoria(10) }
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