package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CrearPlanActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramas
import com.example.plantea.presentacion.viewModels.CrearPlanViewModel
import com.google.android.material.textfield.TextInputLayout

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
        val adaptador = AdaptadorPictogramas(viewModel._listaPictogramas.value, this, this)
        recyclerPictogramas.adapter = adaptador

        //Este método se ejecutará al pinchar sobre la imagen de cerrar
        image_Cerrar.setOnClickListener {
            viewModel._closeFragment.value = true
            Log.d("TAG", "image_Cerrar.setOnClickListener")
        }

        //Este método se ejecutará al seleccionar añadir nuevo pictograma.
        image_add.setOnClickListener { viewModel.nuevoPictogramaDialogo() }

        viewModel._historiaClicked.observe(viewLifecycleOwner) {
            var historiaSaved = false
            val tituloCard = viewModel.listaPlanificacion[it].titulo
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialogo_historiasocial)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val btnGuardar = dialog.findViewById<Button>(R.id.btn_eliminarEvento)
            val cardtitulo = dialog.findViewById<TextView>(R.id.cardName)
            cardtitulo.text = tituloCard
            val iconoCerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)
            val historiaText = dialog.findViewById<TextInputLayout>(R.id.historiaText)

            if (viewModel.listaPlanificacion[it].historia.toString() == "null") {
                historiaText.editText?.setText("")
            } else {
                historiaText.editText?.setText(viewModel.listaPlanificacion[it].historia)
            }

            iconoCerrar.setOnClickListener { dialog.dismiss() }
            btnGuardar.setOnClickListener { view ->
                if (historiaText.editText?.text.toString() == "") {
                    Toast.makeText(
                        requireContext(),
                        "No se puede crear una historia vacía",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    viewModel.listaPlanificacion[it].historia = historiaText.editText?.text.toString()
                    val parentActivity = activity as CrearPlanActivity
                    parentActivity.adaptador.notifyItemChanged(it)
                    dialog.dismiss()
                }
            }

            dialog.show()
        }

        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
        }
    }

    //Este método se ejecutará al seleccionar un pictograma para añadirlo en la planificacion
    override fun onItemSeleccionado(posicion: Int) {
        if (viewModel._listaPictogramas.value?.get(posicion)?.categoria in 1..4) {
            viewModel.mostrarsubCategoria(viewModel._listaPictogramas.value?.get(posicion)?.titulo, this.requireContext())
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


