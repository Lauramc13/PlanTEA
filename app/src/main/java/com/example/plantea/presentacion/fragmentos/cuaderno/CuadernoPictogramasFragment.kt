package com.example.plantea.presentacion.fragmentos.cuaderno

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.CuadernoInterface
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasCuaderno

class CuadernoPictogramasFragment : Fragment(), AdaptadorPictogramasCuaderno.OnItemSelectedListener {
    lateinit var vista: View
    lateinit var actividad: Activity
    lateinit var interfaceCuaderno: CuadernoInterface
    lateinit var listaPictogramas: ArrayList<Pictograma>
    lateinit var lst_Pictogramas: RecyclerView
    lateinit var image_Cerrar: ImageView
    lateinit var seekbar: SeekBar

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val orientation = newConfig.orientation
        val gridValueManager: Int
        gridValueManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            3 // set the number of columns to 3 for portrait mode
        } else {
            5 // set the number of columns to 5 for landscape mode
        }
        lst_Pictogramas.setLayoutManager(GridLayoutManager(context, gridValueManager))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_cuaderno_pictogramas, container, false)
        val bundle = this.arguments
        listaPictogramas = (bundle!!["key"] as ArrayList<Pictograma>?)!!
        lst_Pictogramas = vista.findViewById(R.id.lst_cuaderno_pictogramas)
        val orientation = resources.configuration.orientation
        val gridValueManager: Int = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            3 // set the number of columns to 3 for portrait mode
        } else {
            5 // set the number of columns to 5 for landscape mode
        }
        lst_Pictogramas.layoutManager = GridLayoutManager(context, gridValueManager)
        val adaptador = AdaptadorPictogramasCuaderno(listaPictogramas, this)
        lst_Pictogramas.adapter = adaptador
        image_Cerrar = vista.findViewById(R.id.icono_cuaderno_fragment)
        image_Cerrar.setOnClickListener { interfaceCuaderno.cerrarFragment() }
        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
            interfaceCuaderno = (actividad as CuadernoInterface?)!!
        }
    }

    override fun pictogramaCuaderno(posicion: Int) {
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.dialogo_termometro)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val pictograma = dialog.findViewById<ImageView>(R.id.img_pictograma)
        val tituloPictograma = dialog.findViewById<TextView>(R.id.lbl_pictograma)
        seekbar = dialog.findViewById(R.id.seekBar_termometro)
        pictograma.setImageURI(Uri.parse(listaPictogramas[posicion].imagen))
        tituloPictograma.text = listaPictogramas[posicion].titulo

        //Funcionalidad termómetro: cambio de color según el progreso
        seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Write code to perform some action when progress is changed.
                if (progress < 30) {
                    seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(118, 255, 3))
                } else if (progress < 60) {
                    seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(255, 165, 0))
                } else if (progress < 100) {
                    seekBar.progressTintList = ColorStateList.valueOf(Color.RED)
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
}