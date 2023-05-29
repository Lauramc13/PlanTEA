package com.example.plantea.presentacion.fragmentos.cuaderno

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.plantea.R
import com.example.plantea.presentacion.CuadernoInterface

class PrincipalFragment : Fragment() {
    lateinit var vista: View
    lateinit var actividad: Activity
    lateinit var cardSintomas: CardView
    lateinit var cardDolores: CardView
    lateinit var cardSentimientos: CardView
    lateinit var interfaceCuaderno: CuadernoInterface
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_cuaderno_principal, container, false)
        cardSintomas = vista.findViewById(R.id.card_sintomas)
        cardDolores = vista.findViewById(R.id.card_dolores)
        cardSentimientos = vista.findViewById(R.id.card_sentimientos)
        cardSintomas.setOnClickListener { interfaceCuaderno.mostrarPictogramas(2) }
        cardDolores.setOnClickListener { interfaceCuaderno.mostrarPictogramas(3) }
        cardSentimientos.setOnClickListener { interfaceCuaderno.mostrarPictogramas(4) }
        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
            interfaceCuaderno = (actividad as CuadernoInterface?)!!
        }
    }
}