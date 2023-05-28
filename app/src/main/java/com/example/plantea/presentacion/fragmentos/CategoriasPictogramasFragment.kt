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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.CrearPlanInterface
import com.example.plantea.presentacion.actividades.planificador.CrearPlanActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramas


class CategoriasPictogramasFragment : Fragment(), AdaptadorPictogramas.OnItemSelectedListener {
    lateinit var recyclerPictogramas: RecyclerView
    lateinit var listaPictogramas: ArrayList<Pictograma>
    lateinit var interfaceCategorias: CrearPlanInterface
    lateinit var actividad: Activity
    lateinit var vista: View
    private lateinit var image_Cerrar: ImageView
    private lateinit var image_add: ImageView
    var pictograma = Pictograma()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_categorias_pictogramas, container, false)
        val bundle = this.arguments
        listaPictogramas = (bundle!!["key"] as ArrayList<Pictograma>?)!!
        recyclerPictogramas = vista.findViewById(R.id.recycler_Pictogramas)
        val orientation = resources.configuration.orientation
        val gridValueManager: Int = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            3 // set the number of columns to 3 for portrait mode
        } else {
            5 // set the number of columns to 5 for landscape mode
        }
        recyclerPictogramas.layoutManager = GridLayoutManager(context, gridValueManager, GridLayoutManager.VERTICAL, false)
        image_Cerrar = vista.findViewById(R.id.image_Cerrar)
        image_add = vista.findViewById(R.id.image_add)
        val adaptador = AdaptadorPictogramas(listaPictogramas, this, this)
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

    //Este método se ejecutará al seleccionar un pictograma para añadirlo en la planificacion
    override fun onItemSeleccionado(posicion: Int) {
        if (listaPictogramas[posicion].categoria in 1..4) {
            interfaceCategorias.mostrarsubCategoria(listaPictogramas[posicion].titulo)
        } else {
            interfaceCategorias.pictogramaSeleccionado(listaPictogramas[posicion].titulo, listaPictogramas[posicion].imagen, listaPictogramas[posicion].categoria)
            val planActivity = activity as? CrearPlanActivity
            planActivity?.addPictogram(listaPictogramas[posicion])
        }
    }

    fun markAsFavorite(pictogram: Pictograma) {
        val prefs = requireContext().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val idUsuario = prefs.getString("idUsuario", "")
        pictograma.insertarFavorito(actividad, idUsuario, pictogram.id)
    }

    fun removeFavorite(pictogram: Pictograma, posicion: Int) {
        val prefs = requireContext().getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
        val idUsuario = prefs.getString("idUsuario", "")
        pictograma.borrarFavorito(actividad, idUsuario, pictogram.id)

        // if (listaPictogramas[posicion].categoria == 10) {
        //     // delete in the position from listaPictogramas
        //     listaPictogramas.removeAt(posicion)
        //     // notify the adapter that item is removed
        //     recyclerPictogramas.adapter!!.notifyItemRemoved(posicion)
        //     // notify the adapter that range of item is removed
        //     recyclerPictogramas.adapter!!.notifyItemRangeChanged(posicion, listaPictogramas.size)
        // }
    }

}