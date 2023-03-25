package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.CrearPlanInterface
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramas

class CategoriasPictogramasFragment : Fragment(), AdaptadorPictogramas.OnItemSelectedListener {
    lateinit var recyclerPictogramas: RecyclerView
    lateinit var listaPictogramas: ArrayList<Pictograma>
    lateinit var interfaceCategorias: CrearPlanInterface
    lateinit var actividad: Activity
    lateinit var vista: View
    private lateinit var image_Cerrar: ImageView
    private lateinit var image_add: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_categorias_pictogramas, container, false)
        val bundle = this.arguments
        listaPictogramas = (bundle!!["key"] as ArrayList<Pictograma>?)!!
        recyclerPictogramas = vista.findViewById(R.id.recycler_Pictogramas)
        val orientation = resources.configuration.orientation
        val gridValueManager: Int = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            3 // set the number of columns to 2 for portrait mode
        } else {
            5 // set the number of columns to 3 for landscape mode
        }
        recyclerPictogramas.layoutManager = GridLayoutManager(context, gridValueManager)
        image_Cerrar = vista.findViewById(R.id.image_Cerrar)
        image_add = vista.findViewById(R.id.image_add)
        val adaptador = AdaptadorPictogramas(listaPictogramas, this)
        recyclerPictogramas.adapter = adaptador

        //Este método se ejecutará al pinchar sobre la imagen de cerrar
        image_Cerrar.setOnClickListener { interfaceCategorias.cerrarFragment() }

        //Este método se ejecutará al seleccionar añadir nuevo pictograma.
        image_add.setOnClickListener { interfaceCategorias.nuevoPictogramaDialogo() }
        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
            interfaceCategorias = (actividad as CrearPlanInterface?)!!
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    //Este método se ejecutará al seleccionar un pictograma para añadirlo en la planificacion
    override fun onItemSeleccionado(posicion: Int) {
        if (listaPictogramas[posicion].categoria == 1) {
            interfaceCategorias.mostrarsubCategoria(listaPictogramas[posicion].titulo)
        } else {
            interfaceCategorias.pictogramaSeleccionado(listaPictogramas[posicion].titulo, listaPictogramas[posicion].imagen, listaPictogramas[posicion].categoria)
        }
    }
}