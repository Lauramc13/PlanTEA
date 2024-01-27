package com.example.plantea.presentacion.fragmentos.cuaderno

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasCuaderno
import com.example.plantea.presentacion.viewModels.CuadernoViewModel
import com.google.android.material.imageview.ShapeableImageView

class CuadernoPictogramasFragment : Fragment(), AdaptadorPictogramasCuaderno.OnItemSelectedListener {
    lateinit var actividad: Activity
    private lateinit var lst_Pictogramas: RecyclerView
    private lateinit var image_Cerrar: ImageView
    private lateinit var seekbar: SeekBar
    lateinit var termometro: LinearLayout
    private lateinit var constraintLayout: ConstraintLayout

    private val viewModel: CuadernoViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val vista = inflater.inflate(R.layout.fragment_cuaderno_pictogramas, container, false)
        val bundle = this.arguments

        viewModel.listaPictogramas = (bundle!!["key"] as ArrayList<Pictograma>?)!!
        viewModel.isTermometro = (bundle["termometro"] as Boolean)
        viewModel.tituloCuaderno = (bundle["tituloCuaderno"] as String)

        val txtCuaderno = vista.findViewById<TextView>(R.id.titulo_cuaderno)
        txtCuaderno.text = viewModel.tituloCuaderno

        lst_Pictogramas = vista.findViewById(R.id.lst_cuaderno_pictogramas)
        constraintLayout = vista.findViewById(R.id.frameLayout)
        CommonUtils.getGridValueCuaderno(vista, context, lst_Pictogramas, constraintLayout)

        val prefs = context?.getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val isPlanificador = prefs?.getBoolean("PlanificadorLogged", false)
        val adaptador = isPlanificador?.let { context?.let { it1 -> AdaptadorPictogramasCuaderno(viewModel.listaPictogramas, it, this, it1) }}!!
        lst_Pictogramas.adapter = adaptador
        image_Cerrar = vista.findViewById(R.id.icono_cuaderno_fragment)
        image_Cerrar.setOnClickListener { viewModel._cerrarFragment.value = true}
        context?.let { CommonUtils.initializeTextToSpeech(it) }

        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
        }
    }

    override fun pictogramaCuaderno(posicion: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialogo_termometro)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val pictograma = dialog.findViewById<ShapeableImageView>(R.id.img_pictograma)
        val tituloPictograma = dialog.findViewById<TextView>(R.id.lbl_pictograma)
        val iconoEscuchar = dialog.findViewById<ImageView>(R.id.icono_escuchar)
        seekbar = dialog.findViewById(R.id.seekBar_termometro)
        termometro = dialog.findViewById(R.id.termometro)
        pictograma.setImageURI(Uri.parse(viewModel.listaPictogramas?.get(posicion)?.imagen ))
        tituloPictograma.text = viewModel.listaPictogramas?.get(posicion)?.titulo
        if(!viewModel.isTermometro){
            termometro.visibility = View.GONE
        }

        iconoEscuchar.setOnClickListener {
            CommonUtils.textToSpeechWord(viewModel.listaPictogramas?.get(posicion)?.titulo )
        }

        //Botón cerrar
        image_Cerrar = dialog.findViewById(R.id.icono_CerrarDialogoEvento)
        image_Cerrar.setOnClickListener { dialog.dismiss() }
        dialog.show()

        //Funcionalidad termómetro: cambio de color según el progreso
        seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Write code to perform some action when progress is changed.
                if (progress < 45) {
                    seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(138, 255, 126))
                } else if (progress < 90) {
                    seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(255, 193, 79))
                } else if (progress < 100) {
                    seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(239, 35, 60))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
                // Toast.makeText(getContext(), "Progress is " + seekBar.getProgress() + "%", Toast.LENGTH_SHORT).show();
            }
        })
        dialog.show()
    }

    override fun addPicto(pictograma: Pictograma) {
        TODO("Not yet implemented")
    }

    override fun removePicto(pictograma: Pictograma, sourceAPI: Boolean, isBusqueda: Boolean) {
        TODO("Not yet implemented")
    }


}